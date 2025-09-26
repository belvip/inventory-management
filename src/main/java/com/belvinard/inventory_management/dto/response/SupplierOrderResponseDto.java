package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public record SupplierOrderResponseDto(
        Long id,
        String code,
        LocalDate orderDate,
        String comments,
        OrderStatus stateOrder,
        List<SupplierOrderLineResponseDto> supplierOrderLineList,
        String createdAt,
        String updatedAt
) {
}
