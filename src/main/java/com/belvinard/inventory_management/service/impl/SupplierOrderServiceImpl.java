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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierOrderServiceImpl implements SupplierOrderService {
    private final SupplierOrderRepository supplierOrderRepository;
    private final SupplierOrderMapper supplierOrderMapper;
    private final SupplierRepository supplierRepository;
    private final ArticleRepository articleRepository;
    
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
        
        validateStatusTransition(order.getStateOrder(), newStatus);
        
        order.setStateOrder(newStatus);
        
        // Si passage à COMPLETED, augmenter le stock
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
    
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.COMPLETED) {
            throw new APIException("Cannot change status of a COMPLETED supplier order");
        }
        
        if (current == OrderStatus.CANCELLED) {
            throw new APIException("Cannot change status of a CANCELLED supplier order");
        }
        
        if (current == OrderStatus.PENDING && next != OrderStatus.CONFIRMED && next != OrderStatus.CANCELLED) {
            throw new APIException("From PENDING, can only go to CONFIRMED or CANCELLED");
        }
        
        if (current == OrderStatus.CONFIRMED && next != OrderStatus.COMPLETED && next != OrderStatus.CANCELLED) {
            throw new APIException("From CONFIRMED, can only go to COMPLETED or CANCELLED");
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
