package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.exception.DuplicateResourceException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.ClientOrderMapper;
import com.belvinard.inventory_management.model.Client;
import com.belvinard.inventory_management.model.ClientOrder;
import com.belvinard.inventory_management.model.OrderStatus;
import com.belvinard.inventory_management.repository.ClientOrderRepository;
import com.belvinard.inventory_management.repository.ClientRepository;
import com.belvinard.inventory_management.service.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderMapper clientOrderMapper;
    private final ClientRepository clientRepository;

    @Override
    public ClientOrderResponseDto createOrder(ClientOrderRequestDto orderRequestDto) {

        // 1️⃣ Validate client exists
        Client client = clientRepository.findById(orderRequestDto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + orderRequestDto.clientId() + " not found"));

        // 2️⃣ Check if order code is unique
        if (clientOrderRepository.existsByCode(orderRequestDto.code())) {
            throw new DuplicateResourceException("Order code " + orderRequestDto.code() + " already exists");
        }

        // 3️⃣ Map DTO to entity
        ClientOrder order = clientOrderMapper.toEntity(orderRequestDto);

        // 4️⃣ Set the client reference (important for @ManyToOne relationship)
        order.setClient(client);

        // 5️⃣ Set order date (default: today if null)
        if (orderRequestDto.orderDate() == null) {
            order.setOrderDate(LocalDate.now());
        } else {
            order.setOrderDate(orderRequestDto.orderDate());
        }

        // 6️⃣ Force default status
        order.setStateOrder(OrderStatus.IN_PREPARATION);

        // 7️⃣ Save
        ClientOrder saved = clientOrderRepository.save(order);

        // 8️⃣ Return response DTO
        return clientOrderMapper.toResponseDto(saved);
    }
}

