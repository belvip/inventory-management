package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;

public interface SaleService {
    SaleResponseDto createSale(SaleRequestDto dto);

}
