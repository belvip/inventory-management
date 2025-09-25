package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.SaleMapper;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.ClientOrderRepository;
import com.belvinard.inventory_management.repository.ClientRepository;
import com.belvinard.inventory_management.repository.SaleRepository;
import com.belvinard.inventory_management.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponseDto createSale(SaleRequestDto dto) {
        validateSaleRequest(dto);
        
        Client client = findAndValidateClient(dto.clientId());
        validateClientHasOrders(client.getId());
        
        Sale sale = createSaleEntity(dto, client);
        Sale savedSale = saleRepository.save(sale);
        
        return saleMapper.toResponseDto(savedSale);
    }

    @Override
    @Transactional
    public SaleResponseDto updateSale(Long id, SaleRequestDto dto) {
        Sale sale = findSaleById(id);
        validateSaleNotConfirmed(sale, "update");
        
        // Mise à jour uniquement des champs autorisés (pas le statut)
        if (dto.comments() != null) {
            sale.setComments(dto.comments());
        }
        if (dto.saleDate() != null) {
            sale.setSaleDate(dto.saleDate());
        }
        // Le statut n'est PAS mis à jour ici - utiliser updateSaleStatus() à la place
        
        Sale updatedSale = saleRepository.save(sale);
        return saleMapper.toResponseDto(updatedSale);
    }

    @Override
    @Transactional
    public void deleteSale(Long id) {
        Sale sale = findSaleById(id);
        validateSaleNotConfirmed(sale, "delete");
        
        saleRepository.delete(sale);
    }

    @Override
    @Transactional
    public SaleResponseDto updateSaleStatus(Long id, String status) {
        Sale sale = findSaleById(id);
        SaleStatus newStatus = SaleStatus.valueOf(status.toUpperCase());
        
        validateStatusTransition(sale, newStatus);
        
        sale.setSaleStatus(newStatus);
        Sale updatedSale = saleRepository.save(sale);
        
        return saleMapper.toResponseDto(updatedSale);
    }

    @Override
    @Transactional
    public SaleResponseDto cancelSale(Long id) {
        Sale sale = findSaleById(id);
        validateSaleNotConfirmed(sale, "cancel");
        
        sale.setSaleStatus(SaleStatus.CANCELLED);
        Sale cancelledSale = saleRepository.save(sale);
        
        return saleMapper.toResponseDto(cancelledSale);
    }

    @Override
    @Transactional
    public SaleResponseDto finalizeSale(Long id) {
        Sale sale = findSaleById(id);
        
        if (sale.getSaleStatus() != SaleStatus.CONFIRMED) {
            throw new APIException("Sale must be CONFIRMED before finalization");
        }
        
        // Finaliser la vente (décrémenter stock, etc.)
        processSaleFinalization(sale);
        
        Sale finalizedSale = saleRepository.save(sale);
        return saleMapper.toResponseDto(finalizedSale);
    }

    @Override
    public SaleResponseDto getSaleById(Long id) {
        Sale sale = findSaleById(id);
        return saleMapper.toResponseDto(sale);
    }

    @Override
    public List<SaleResponseDto> getAll() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .map(saleMapper::toResponseDto)
                .toList();
    }


    private void validateSaleRequest(SaleRequestDto dto) {
        if (dto.status() != SaleStatus.DRAFT) {
            throw new APIException("Sale status must be DRAFT at creation");
        }
    }
    
    private Client findAndValidateClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
    }
    
    private void validateClientHasOrders(Long clientId) {
        List<ClientOrder> confirmedOrders = clientOrderRepository.findByClientIdAndStateOrder(clientId, OrderStatus.CONFIRMED);
        if (confirmedOrders.isEmpty()) {
            throw new APIException("Client must have at least one CONFIRMED order before creating a sale");
        }
        
        validateNoExistingDraftSale(clientId);
    }
    
    private void validateNoExistingDraftSale(Long clientId) {
        boolean hasDraftSale = saleRepository.existsByClientIdAndSaleStatus(clientId, SaleStatus.DRAFT);
        if (hasDraftSale) {
            throw new APIException("Client already has a DRAFT sale in progress");
        }
    }
    
    private Sale createSaleEntity(SaleRequestDto dto, Client client) {
        Sale sale = saleMapper.toEntity(dto);
        sale.setClient(client);
        sale.setSaleStatus(SaleStatus.DRAFT);
        
        if (dto.saleDate() == null) {
            sale.setSaleDate(LocalDate.now());
        }
        
        return sale;
    }
    
    private Sale findSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
    }
    
    private void validateSaleNotConfirmed(Sale sale, String operation) {
        if (sale.getSaleStatus() == SaleStatus.CONFIRMED) {
            throw new APIException("Cannot " + operation + " a CONFIRMED sale");
        }
    }
    
    private void validateStatusTransition(Sale sale, SaleStatus next) {
        SaleStatus current = sale.getSaleStatus();
        
        if (current == SaleStatus.CONFIRMED && next != SaleStatus.CANCELLED) {
            throw new APIException("CONFIRMED sales can only be CANCELLED");
        }
        
        if (current == SaleStatus.CANCELLED) {
            throw new APIException("Cannot change status of a CANCELLED sale");
        }
        
        // Validation spéciale pour la confirmation de vente
        if (current == SaleStatus.DRAFT && next == SaleStatus.CONFIRMED) {
            validateClientOrdersCompleted(sale.getClient().getId());
        }
    }
    
    private void validateClientOrdersCompleted(Long clientId) {
        List<ClientOrder> clientOrders = clientOrderRepository.findByClientId(clientId);
        
        boolean hasCompletedOrders = clientOrders.stream()
                .anyMatch(order -> order.getStateOrder() == OrderStatus.COMPLETED);
                
        if (!hasCompletedOrders) {
            throw new APIException("Sale cannot be CONFIRMED: Client must have at least one COMPLETED order");
        }
    }
    
    private void processSaleFinalization(Sale sale) {
        // Logique de finalisation :
        // - Décrémenter le stock des articles
        // - Créer les lignes de vente définitives
        // - Générer le code de vente
        // TODO: Implémenter selon les besoins métier
    }
    
    @Override
    @Transactional
    public SaleResponseDto generateSaleLinesFromOrders(Long saleId) {
        Sale sale = findSaleById(saleId);
        
        if (sale.getSaleStatus() == SaleStatus.CANCELLED) {
            throw new APIException("Cannot generate lines for CANCELLED sales");
        }
        
        // Vider les lignes existantes si elles existent
        sale.getSaleLines().clear();
        
        // Récupérer les commandes COMPLETED du client
        List<ClientOrder> completedOrders = clientOrderRepository
                .findByClientIdAndStateOrder(sale.getClient().getId(), OrderStatus.COMPLETED);
        
        // Générer les lignes de vente automatiquement
        completedOrders.forEach(order -> 
            order.getOrderClientLineList().forEach(orderLine -> 
                createSaleLineFromOrderLine(sale, orderLine)
            )
        );
        
        Sale updatedSale = saleRepository.save(sale);
        return saleMapper.toResponseDto(updatedSale);
    }
    
    private void createSaleLineFromOrderLine(Sale sale, OrderClientLine orderLine) {
        SaleLine saleLine = new SaleLine();
        Article article = orderLine.getArticle();
        
        saleLine.setSale(sale);
        saleLine.setArticle(article);
        saleLine.setQuantity(orderLine.getQuantity());
        
        // Récupérer les prix de l'article (prix figés au moment de la vente)
        saleLine.setUnitPriceExclTax(article.getUnitPriceExclTax());
        saleLine.setRateTva(article.getRateTva());
        saleLine.setUnitPriceAllTax(article.getUnitPriceAllTax());
        
        // Le prix total sera calculé automatiquement par @PrePersist
        
        sale.getSaleLines().add(saleLine);
    }
}
