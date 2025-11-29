package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.SupplierOrderRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderResponseDto;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.model.OrderStatus;
import com.belvinard.inventory_management.mapper.SupplierOrderMapper;
import com.belvinard.inventory_management.model.Article;
import com.belvinard.inventory_management.model.Supplier;
import com.belvinard.inventory_management.model.SupplierOrder;
import com.belvinard.inventory_management.repository.ArticleRepository;
import com.belvinard.inventory_management.repository.SupplierOrderRepository;
import com.belvinard.inventory_management.repository.SupplierRepository;
import com.belvinard.inventory_management.service.SupplierOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SupplierOrderServiceImpl implements SupplierOrderService {
    
    private static final int MAX_MODIFICATION_DAYS = 7;
    
    private final SupplierOrderRepository supplierOrderRepository;
    private final SupplierOrderMapper supplierOrderMapper;
    private final SupplierRepository supplierRepository;
    private final ArticleRepository articleRepository;
    
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.PENDING),
            OrderStatus.CANCELLED, Set.of(OrderStatus.PENDING),
            OrderStatus.COMPLETED, Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CANCELLED)
    );
    
    @Override
    @Transactional
    public SupplierOrderResponseDto create(SupplierOrderRequestDto dto, Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));

        supplierOrderRepository.findByCode(dto.code())
                .ifPresent(existing -> {
                    throw new APIException("Supplier order already exists with code: " + dto.code());
                });
        
        SupplierOrder supplierOrder = supplierOrderMapper.toEntity(dto);
        supplierOrder.setSupplier(supplier);
        if (dto.orderDate() == null) supplierOrder.setOrderDate(LocalDate.now());
        
        SupplierOrder savedOrder = supplierOrderRepository.save(supplierOrder);
        return supplierOrderMapper.toResponseDto(savedOrder);
    }

    @Override
    public SupplierOrderResponseDto getById(Long id) {
        SupplierOrder order = findOrderById(id);
        return supplierOrderMapper.toResponseDto(order);
    }

    @Override
    @Transactional
    public SupplierOrderResponseDto update(Long id, SupplierOrderRequestDto dto) {
        SupplierOrder order = findOrderById(id);
        validateNotConfirmed(order, "update");

        supplierOrderRepository.findByCode(dto.code())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new APIException("Supplier order already exists with code: " + dto.code());
                });
        
        order.setCode(dto.code());
        order.setOrderDate(dto.orderDate());
        order.setComments(dto.comments());
        
        SupplierOrder updatedOrder = supplierOrderRepository.save(order);
        return supplierOrderMapper.toResponseDto(updatedOrder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SupplierOrder order = findOrderById(id);
        validateNotConfirmed(order, "delete");
        supplierOrderRepository.delete(order);
    }

    @Override
    @Transactional
    public SupplierOrderResponseDto updateState(Long id, String state) {
        SupplierOrder order = findOrderById(id);
        OrderStatus newStatus = OrderStatus.valueOf(state.toUpperCase());
        
        validateStatusChangeRules(order, newStatus);
        
        order.setStateOrder(newStatus);
        
        if (newStatus == OrderStatus.COMPLETED) {
            increaseStockFromOrder(order);
        }
        
        SupplierOrder updatedOrder = supplierOrderRepository.save(order);
        return supplierOrderMapper.toResponseDto(updatedOrder);
    }

    @Override
    @Transactional
    public SupplierOrderResponseDto cancel(Long id) {
        SupplierOrder order = findOrderById(id);
        validateCanCancel(order);
        
        order.setStateOrder(OrderStatus.CANCELLED);
        SupplierOrder cancelledOrder = supplierOrderRepository.save(order);
        return supplierOrderMapper.toResponseDto(cancelledOrder);
    }

    @Override
    public SupplierOrderResponseDto findByCode(String code) {
        SupplierOrder order = supplierOrderRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with code: " + code));
        return supplierOrderMapper.toResponseDto(order);
    }

    @Override
    public List<SupplierOrderResponseDto> getAll() {
        return supplierOrderRepository.findAll()
                .stream()
                .map(supplierOrderMapper::toResponseDto)
                .toList();
    }
    
    private SupplierOrder findOrderById(Long id) {
        return supplierOrderRepository.findByIdWithLines(id)
                .orElse(supplierOrderRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + id)));
    }
    
    private void validateNotConfirmed(SupplierOrder order, String operation) {
        if (order.getStateOrder() == OrderStatus.CONFIRMED) {
            throw new APIException("Cannot " + operation + " a CONFIRMED supplier order");
        }
    }
    
    private void validateStatusChangeRules(SupplierOrder order, OrderStatus newStatus) {
        OrderStatus current = order.getStateOrder();
        
        validateAllowedTransition(current, newStatus);
        validateTimeConstraints(order, current, newStatus);
        validateCompletionRequirements(order, newStatus);
    }
    
    private void validateAllowedTransition(OrderStatus current, OrderStatus next) {
        if (!ALLOWED_TRANSITIONS.get(current).contains(next)) {
            throw new APIException("Invalid transition from " + current + " to " + next);
        }
    }
    
    private void validateTimeConstraints(SupplierOrder order, OrderStatus currentStatus, OrderStatus newStatus) {
        // Seulement appliquer la contrainte de temps pour les transitions vers un statut antérieur
        if (!isBackwardTransition(currentStatus, newStatus)) {
            return;
        }
        
        if (isOrderModificationExpired(order)) {
            throw new APIException(
                    "Cannot revert to previous status after " + MAX_MODIFICATION_DAYS +
                            " days. Current: " + currentStatus + ", Requested: " + newStatus
            );
        }
    }
    
    private boolean isBackwardTransition(OrderStatus current, OrderStatus next) {
        // Définir les transitions "vers l'arrière" (retour à un statut antérieur)
        return (current == OrderStatus.CONFIRMED && next == OrderStatus.PENDING) ||
               (current == OrderStatus.COMPLETED && next == OrderStatus.CONFIRMED) ||
               (current == OrderStatus.CANCELLED && next == OrderStatus.PENDING) ||
               (current == OrderStatus.COMPLETED && next == OrderStatus.PENDING);
    }
    
    private boolean isOrderModificationExpired(SupplierOrder order) {
        LocalDate statusChangeDate = getStatusChangeDate(order);
        long daysSinceStatusChange = ChronoUnit.DAYS.between(statusChangeDate, LocalDate.now());
        return daysSinceStatusChange > MAX_MODIFICATION_DAYS;
    }
    
    private LocalDate getStatusChangeDate(SupplierOrder order) {
        return Optional.ofNullable(order.getUpdatedDate())
                .orElse(Optional.ofNullable(order.getCreatedDate())
                        .orElse(order.getOrderDate()));
    }
    
    private void validateCompletionRequirements(SupplierOrder order, OrderStatus next) {
        if (next != OrderStatus.COMPLETED) {
            return;
        }
        
        if (order.getSupplierOrderLineList() == null || order.getSupplierOrderLineList().isEmpty()) {
            throw new APIException("Cannot complete order without order lines.");
        }
    }
    
    private void validateCanCancel(SupplierOrder order) {
        if (order.getStateOrder() == OrderStatus.COMPLETED) {
            throw new APIException("Cannot cancel a COMPLETED supplier order - stock has been updated");
        }
        
        if (order.getStateOrder() == OrderStatus.CANCELLED) {
            throw new APIException("Supplier order is already CANCELLED");
        }
        
    
    }
    
    private void increaseStockFromOrder(SupplierOrder order) {
        if (order.getSupplierOrderLineList() != null) {
            order.getSupplierOrderLineList().forEach(line -> {
                if (line.getArticle() != null) {
                    Article article = line.getArticle();
                    Long currentStock = article.getQuantityInStock();
                    Long quantityToAdd = line.getQuantity().longValue();
                    article.setQuantityInStock(currentStock + quantityToAdd);
                    articleRepository.save(article);
                }
            });
        }
    }
}
