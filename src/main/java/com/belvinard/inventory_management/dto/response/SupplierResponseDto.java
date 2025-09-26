package com.belvinard.inventory_management.dto.response;

import java.time.LocalDate;
import java.util.List;

public record SupplierResponseDto(
        Long id,
        String name,
        String phoneNumber,
        List<SupplierOrderResponseDto> supplierOrders,
        LocalDate createdAt,
        LocalDate updatedAt

) {
}
