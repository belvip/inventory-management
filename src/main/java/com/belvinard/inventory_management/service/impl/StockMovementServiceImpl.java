package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.*;
import com.belvinard.inventory_management.service.StockMovementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {
    
    private final StockMovementRepository stockMovementRepository;
    private final SupplierOrderRepository supplierOrderRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final SaleRepository saleRepository;
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
}
