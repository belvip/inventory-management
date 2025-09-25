package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.response.SupplierResponseDto;

public interface SupplierService {
    SupplierResponseDto createSupplier(SupplierResponseDto dto);
}
