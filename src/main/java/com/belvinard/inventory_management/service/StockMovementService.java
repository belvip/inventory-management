package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.StockMovementRequestDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.dto.response.StockMovementResponseDto;
import com.belvinard.inventory_management.model.MvtOrigin;
import com.belvinard.inventory_management.model.StockMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockMovementService {

    // Méthodes de création automatique
    void createStockMovementForOrder(Long supplierOrderId);
    void createStockMovementClientOrder(Long clientOrderId);
    void createStockMovementForSale(Long saleId);

}
