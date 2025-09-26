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
    StockMovementResponseDto createMovementForSupplierOrder(Long articleId, Long quantity, Long supplierOrderId, String description);
    StockMovementResponseDto createMovementForClientOrder(Long articleId, Long quantity, Long clientOrderId, String description);
    StockMovementResponseDto createManualAdjustment(Long articleId, Long quantity, StockMovementType type, String description);
    
    // Méthodes de consultation
    List<StockMovementResponseDto> getMovementsByArticle(Long articleId);
    Page<StockMovementResponseDto> getMovementsByType(StockMovementType type, Pageable pageable);
    Page<StockMovementResponseDto> getMovementsByOrigin(MvtOrigin origin, Pageable pageable);
    
    // Méthodes de statistiques
    Long getTotalInMovements(Long articleId);
    Long getTotalOutMovements(Long articleId);
    List<StockMovementResponseDto> getRecentMovements(int limit);

    // Consultation uniquement (les mouvements sont automatiques)
    StockMovementResponseDto getById(Long id);

    PagedResponse getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

}
