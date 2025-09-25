package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.SupplierRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierResponseDto;

import java.util.List;

public interface SupplierService {
    SupplierResponseDto createSupplier(SupplierRequestDto dto, Long companyId);
    SupplierResponseDto getSupplierById(Long id);
    SupplierResponseDto updateSupplier(Long id, SupplierRequestDto dto);
    List<SupplierResponseDto> getAllSuppliers();
    void deleteSupplier(Long id);
}
