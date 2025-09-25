package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.model.Client;
import com.belvinard.inventory_management.model.ClientOrder;
import com.belvinard.inventory_management.model.OrderStatus;
import com.belvinard.inventory_management.model.SaleStatus;
import com.belvinard.inventory_management.repository.ClientOrderRepository;
import com.belvinard.inventory_management.repository.ClientRepository;
import com.belvinard.inventory_management.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleValidator {

    private final ClientRepository clientRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final SaleRepository saleRepository;

    public void validateSaleRequest(SaleRequestDto dto) {
        if (dto.status() != SaleStatus.DRAFT) {
            throw new APIException("Sale status must be DRAFT at creation");
        }
    }

    public Client findAndValidateClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
    }

    public void validateNoExistingDraftSale(Long clientId) {
        if (saleRepository.existsByClientIdAndSaleStatus(clientId, SaleStatus.DRAFT)) {
            throw new APIException("Client already has a DRAFT sale in progress");
        }
    }

    public ClientOrder findClientOrder(Long clientOrderId) {
        return clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Client Order not found with id: " + clientOrderId));
    }

    public void validateClientOrder(ClientOrder order, Long clientId) {
        if (order.getStateOrder() != OrderStatus.CONFIRMED) {
            throw new APIException("Only CONFIRMED orders can be converted to sales");
        }
        if (!order.getClient().getId().equals(clientId)) {
            throw new APIException("Client Order does not belong to the specified client");
        }
    }
}