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
}