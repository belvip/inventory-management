package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;

import java.util.List;

public interface SaleService {
    SaleResponseDto createSale(SaleRequestDto dto);
    SaleResponseDto updateSale(Long id, SaleRequestDto dto);
    void deleteSale(Long id);
    SaleResponseDto updateSaleStatus(Long id, String status);
    SaleResponseDto cancelSale(Long id);
    SaleResponseDto finalizeSale(Long id);
    SaleResponseDto getSaleById(Long id);
    List<SaleResponseDto> getAll();
    SaleResponseDto generateSaleLinesFromOrders(Long saleId);


}
