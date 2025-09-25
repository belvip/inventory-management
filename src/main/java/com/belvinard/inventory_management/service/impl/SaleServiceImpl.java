package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.SaleMapper;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.ClientOrderRepository;
import com.belvinard.inventory_management.repository.SaleRepository;
import com.belvinard.inventory_management.service.SaleLineService;
import com.belvinard.inventory_management.service.SaleService;
import com.belvinard.inventory_management.service.SaleValidator;
import com.belvinard.inventory_management.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final SaleMapper saleMapper;
    private final SaleValidator saleValidator;
    private final SaleLineService saleLineService;
    private final StockService stockService;

    @Override
    @Transactional
    public SaleResponseDto createSale(SaleRequestDto dto) {
        saleValidator.validateSaleRequest(dto);
        Client client = saleValidator.findAndValidateClient(dto.clientId());
        saleValidator.validateNoExistingDraftSale(client.getId());

        ClientOrder order = saleValidator.findClientOrder(dto.clientOrderId());
        saleValidator.validateClientOrder(order, client.getId());

        Sale sale = saleMapper.toEntity(dto);
        sale.setClient(client);
        sale.setClientOrder(order);
        sale.setSaleStatus(SaleStatus.DRAFT);
        if (dto.saleDate() == null) sale.setSaleDate(LocalDate.now());

        return saleMapper.toResponseDto(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponseDto updateSale(Long id, SaleRequestDto dto) {
        Sale sale = findSaleById(id);
        if (sale.getSaleStatus() == SaleStatus.CONFIRMED) {
            throw new APIException("Cannot update a CONFIRMED sale");
        }

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
        if (sale.getSaleStatus() == SaleStatus.CONFIRMED) {
            throw new APIException("Cannot delete a CONFIRMED sale");
        }

        saleRepository.delete(sale);
    }


    @Override
    @Transactional
    public SaleResponseDto updateSaleStatus(Long id, String status) {
        Sale sale = findSaleById(id);
        if (sale.getSaleStatus() == SaleStatus.CONFIRMED) {
            throw new APIException("Cannot delete a CONFIRMED sale");
        }
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
        if (sale.getSaleStatus() == SaleStatus.CONFIRMED) {
            throw new APIException("Cannot cancel a CONFIRMED sale");
        }

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

        saleLineService.generateFromOrder(sale, sale.getClientOrder());
        sale.getSaleLines().forEach(stockService::consumeStock);

        sale.getClientOrder().setStateOrder(com.belvinard.inventory_management.model.OrderStatus.COMPLETED);
        clientOrderRepository.save(sale.getClientOrder());

        return saleMapper.toResponseDto(saleRepository.save(sale));
    }

    @Override
    public SaleResponseDto getSaleById(Long id) {
        return saleMapper.toResponseDto(findSaleById(id));
    }

    @Override
    public List<SaleResponseDto> getAll() {
        return saleRepository.findAll()
                .stream().map(saleMapper::toResponseDto).toList();
    }

    @Override
    public SaleResponseDto generateSaleLinesFromOrders(Long saleId) {
        return null;
    }

    private Sale findSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
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

    private void validateSaleNotConfirmed(Sale sale, String operation) {
        if (sale.getSaleStatus() == SaleStatus.CONFIRMED) {
            throw new APIException("Cannot " + operation + " a CONFIRMED sale");
        }
    }





}




