package com.belvinard.inventory_management.service;


import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.dto.response.StockMovementResponseDto;
import com.belvinard.inventory_management.model.StockMovementType;

public interface StockMovementService {

    // Méthodes de création automatique
    void createStockMovementForOrder(Long supplierOrderId);
    void createStockMovementClientOrder(Long clientOrderId);
    void createStockMovementForSale(Long saleId);

    StockMovementResponseDto getById(Long id);
    PagedResponse<StockMovementResponseDto> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    StockMovementResponseDto createMovementForSupplierOrder(Long articleId, Long quantity,
                                                            Long supplierOrderId, String description);
    StockMovementResponseDto createMovementForClientOrder(Long articleId, Long quantity,
                                                          Long clientOrderId, String description);
    StockMovementResponseDto createManualAdjustment(Long articleId, Long quantity, StockMovementType type,
                                                    String description);

    // Méthodes de consultation spécialisées
    PagedResponse<StockMovementResponseDto> getAllInMovements(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    PagedResponse<StockMovementResponseDto> getAllOutMovements(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    java.util.List<StockMovementResponseDto> getInMovementsByArticle(Long articleId);
    java.util.List<StockMovementResponseDto> getOutMovementsByArticle(Long articleId);

}
