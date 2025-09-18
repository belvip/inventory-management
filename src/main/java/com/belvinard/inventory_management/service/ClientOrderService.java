package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.dto.response.ClientResponseDto;
import com.belvinard.inventory_management.model.OrderStatus;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public interface ClientOrderService {
    ClientOrderResponseDto createOrder(ClientOrderRequestDto orderRequestDto);
    ClientOrderResponseDto getOrderById(Long id);
    ClientOrderResponseDto updateOrder(Long id, ClientOrderRequestDto orderRequestDto);
    List<ClientOrderResponseDto> getOrdersByClient(Long clientId);
    void deleteOrder(Long id);
    // Specific business logic methods
    ClientOrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus);
    List<ClientOrderResponseDto> getOrdersByStatus(OrderStatus status);
    List<ClientOrderResponseDto> getAllOrders();


}
