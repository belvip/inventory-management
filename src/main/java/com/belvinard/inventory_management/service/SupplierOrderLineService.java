package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.SupplierOrderLineRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderLineResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface SupplierOrderLineService {
    SupplierOrderLineResponseDto addLineToOrder(SupplierOrderLineRequestDto dto);
    SupplierOrderLineResponseDto getLineById(Long id);
    List<SupplierOrderLineResponseDto> getAllLinesForOrder(Long supplierOrderId);
    SupplierOrderLineResponseDto update(Long id, SupplierOrderLineRequestDto dto);
    SupplierOrderLineResponseDto updateLineQuantity(Long id, BigDecimal newQuantity);
    void removeLineFromOrder(Long id);
    BigDecimal calculateOrderTotal(Long supplierOrderId);
    boolean isArticleAlreadyInOrder(Long supplierOrderId, Long articleId);
    List<SupplierOrderLineResponseDto> getAll();
}
