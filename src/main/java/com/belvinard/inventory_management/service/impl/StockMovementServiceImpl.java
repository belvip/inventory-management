package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.dto.response.StockMovementResponseDto;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.*;
import com.belvinard.inventory_management.service.StockMovementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.belvinard.inventory_management.mapper.StockMovementMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {
    
    private final StockMovementRepository stockMovementRepository;
    private final SupplierOrderRepository supplierOrderRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final SaleRepository saleRepository;
    private final ArticleRepository articleRepository;
    private final StockMovementMapper stockMovementMapper;
    @Override
    @Transactional
    public void createStockMovementForOrder(Long supplierOrderId) {
        SupplierOrder order = supplierOrderRepository.findByIdWithLines(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + supplierOrderId));
        
        order.getSupplierOrderLineList().forEach(line -> {
            StockMovement movement = new StockMovement();
            movement.setQuantity(line.getQuantity().longValue());
            movement.setDescription("Réception commande fournisseur " + order.getCode());
            movement.setMovementType(StockMovementType.IN);
            movement.setMvtOrigin(MvtOrigin.SUPPLIER_ORDER);
            movement.setArticle(line.getArticle());
            movement.setSourceId(supplierOrderId);
            
            stockMovementRepository.save(movement);
        });
    }

    @Override
    @Transactional
    public void createStockMovementClientOrder(Long clientOrderId) {
        ClientOrder order = clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Client order not found with id: " + clientOrderId));
        
        order.getOrderClientLineList().forEach(line -> {
            StockMovement movement = new StockMovement();
            movement.setQuantity(line.getQuantity().longValue());
            movement.setDescription("Commande client " + order.getCode());
            movement.setMovementType(StockMovementType.OUT);
            movement.setMvtOrigin(MvtOrigin.CLIENT_ORDER);
            movement.setArticle(line.getArticle());
            movement.setSourceId(clientOrderId);
            
            stockMovementRepository.save(movement);
        });
    }

    @Override
    @Transactional
    public void createStockMovementForSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + saleId));
        
        sale.getSaleLines().forEach(line -> {
            StockMovement movement = new StockMovement();
            movement.setQuantity(line.getQuantity().longValue());
            movement.setDescription("Vente " + sale.getId());
            movement.setMovementType(StockMovementType.OUT);
            movement.setMvtOrigin(MvtOrigin.SALES);
            movement.setArticle(line.getArticle());
            movement.setSourceId(saleId);
            
            stockMovementRepository.save(movement);
        });
    }

    @Override
    public StockMovementResponseDto getById(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id: " + id));
        return stockMovementMapper.toResponseDto(movement);
    }

    @Override
    public PagedResponse<StockMovementResponseDto> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<StockMovement> movementPage = stockMovementRepository.findAll(pageable);
        
        List<StockMovementResponseDto> movements = movementPage.getContent()
                .stream()
                .map(stockMovementMapper::toResponseDto)
                .toList();
        
        return PagedResponse.<StockMovementResponseDto>builder()
                .content(movements)
                .pageNumber(movementPage.getNumber())
                .pageSize(movementPage.getSize())
                .totalElements(movementPage.getTotalElements())
                .totalPages(movementPage.getTotalPages())
                .last(movementPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public StockMovementResponseDto createMovementForSupplierOrder(Long articleId, Long quantity, Long supplierOrderId, String description) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        
        StockMovement movement = new StockMovement();
        movement.setQuantity(quantity);
        movement.setDescription(description);
        movement.setMovementType(StockMovementType.IN);
        movement.setMvtOrigin(MvtOrigin.SUPPLIER_ORDER);
        movement.setArticle(article);
        movement.setSourceId(supplierOrderId);
        
        StockMovement savedMovement = stockMovementRepository.save(movement);
        return stockMovementMapper.toResponseDto(savedMovement);
    }

    @Override
    @Transactional
    public StockMovementResponseDto createMovementForClientOrder(Long articleId, Long quantity, Long clientOrderId, String description) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        
        StockMovement movement = new StockMovement();
        movement.setQuantity(quantity);
        movement.setDescription(description);
        movement.setMovementType(StockMovementType.OUT);
        movement.setMvtOrigin(MvtOrigin.CLIENT_ORDER);
        movement.setArticle(article);
        movement.setSourceId(clientOrderId);
        
        StockMovement savedMovement = stockMovementRepository.save(movement);
        return stockMovementMapper.toResponseDto(savedMovement);
    }

    @Override
    @Transactional
    public StockMovementResponseDto createManualAdjustment(Long articleId, Long quantity, StockMovementType type, String description) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        
        StockMovement movement = new StockMovement();
        movement.setQuantity(quantity);
        movement.setDescription(description);
        movement.setMovementType(type);
        movement.setMvtOrigin(MvtOrigin.MANUAL_ADJUSTMENT);
        movement.setArticle(article);
        movement.setSourceId(null);
        
        StockMovement savedMovement = stockMovementRepository.save(movement);
        return stockMovementMapper.toResponseDto(savedMovement);
    }
}
