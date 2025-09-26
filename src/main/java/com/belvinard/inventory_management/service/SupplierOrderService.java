package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.SupplierOrderRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderResponseDto;

public interface SupplierOrderService {
    SupplierOrderResponseDto create(SupplierOrderRequestDto dto, Long SupplierId);
    SupplierOrderResponseDto getById(Long id);
    SupplierOrderResponseDto update(Long id, SupplierOrderRequestDto dto);
    void delete(Long id);
    SupplierOrderResponseDto updateState(Long id, String state);
    SupplierOrderResponseDto cancel(Long id);
    SupplierOrderResponseDto findByCode(String code);
}
