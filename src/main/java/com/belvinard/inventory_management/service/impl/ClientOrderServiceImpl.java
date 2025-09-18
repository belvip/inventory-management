package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.exception.DuplicateResourceException;
import com.belvinard.inventory_management.exception.ResourceConflictException;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderMapper clientOrderMapper;
    private final ClientRepository clientRepository;

    @Override
    public ClientOrderResponseDto createOrder(ClientOrderRequestDto orderRequestDto) {

        Client client = clientRepository.findById(orderRequestDto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + orderRequestDto.clientId() + " not found"));

        if (clientOrderRepository.existsByCode(orderRequestDto.code())) {
            throw new DuplicateResourceException("Order code " + orderRequestDto.code() + " already exists");
        }

        ClientOrder order = clientOrderMapper.toEntity(orderRequestDto);

        order.setClient(client);

        if (orderRequestDto.orderDate() == null) {
            order.setOrderDate(LocalDate.now());
        } else {
            order.setOrderDate(orderRequestDto.orderDate());
        }

        order.setStateOrder(OrderStatus.IN_PREPARATION);

        ClientOrder saved = clientOrderRepository.save(order);

        return clientOrderMapper.toResponseDto(saved);
    }

    @Override
    public ClientOrderResponseDto getOrderById(Long id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return clientOrderMapper.toResponseDto(order);
    }

    @Override
    public ClientOrderResponseDto updateOrder(Long id, ClientOrderRequestDto dto) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // ✅ DO NOT update stateOrder here — business logic enforces dedicated endpoint
        order.setCode(dto.code());
        order.setComments(dto.comments());
        order.setOrderDate(dto.orderDate() != null
                ? dto.orderDate()
                : order.getOrderDate());

        ClientOrder updated = clientOrderRepository.save(order);
        return clientOrderMapper.toResponseDto(updated);
    }



    @Override
    public List<ClientOrderResponseDto> getOrdersByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        return clientOrderRepository.findByClient(client).stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteOrder(Long id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStateOrder() == OrderStatus.DELIVERED || order.getStateOrder() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Orders with status " + order.getStateOrder() + " cannot be deleted.");
        }

        clientOrderRepository.delete(order);
    }

    @Override
    public ClientOrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderStatus currentStatus = order.getStateOrder();

        validateStatusTransition(currentStatus, newStatus);

        order.setStateOrder(newStatus);
        ClientOrder updatedOrder = clientOrderRepository.save(order);

        return clientOrderMapper.toResponseDto(updatedOrder);
    }


    @Override
    public List<ClientOrderResponseDto> getOrdersByStatus(OrderStatus status) {
        List<ClientOrder> orders = clientOrderRepository.findByStateOrder(status);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found with status: " + status);
        }
        return orders.stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ClientOrderResponseDto> getAllOrders() {
        return clientOrderRepository.findAll().stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }


    private void validateStatusTransition(OrderStatus current, OrderStatus next) {

        if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot change status of a delivered or canceled order.");
        }

        if (current == OrderStatus.IN_PREPARATION && next != OrderStatus.VALIDATED) {
            throw new IllegalStateException("Order must first be VALIDATED before moving to " + next);
        }

        if (current == OrderStatus.VALIDATED &&
                (next != OrderStatus.DELIVERED && next != OrderStatus.CANCELED)) {
            throw new IllegalStateException("Order can only be DELIVERED or CANCELED after VALIDATION.");
        }
    }

}

