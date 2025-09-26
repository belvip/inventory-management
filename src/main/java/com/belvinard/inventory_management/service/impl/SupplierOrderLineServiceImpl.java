package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.SupplierOrderLineRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderLineResponseDto;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.SupplierOrderLineMapper;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.ArticleRepository;
import com.belvinard.inventory_management.repository.SupplierOrderLineRepository;
import com.belvinard.inventory_management.repository.SupplierOrderRepository;
import com.belvinard.inventory_management.service.SupplierOrderLineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierOrderLineServiceImpl implements SupplierOrderLineService {

    private final SupplierOrderLineRepository supplierOrderLineRepository;
    private final SupplierOrderRepository supplierOrderRepository;
    private final ArticleRepository articleRepository;
    private final SupplierOrderLineMapper supplierOrderLineMapper;

    @Override
    @Transactional
    public SupplierOrderLineResponseDto addLineToOrder(SupplierOrderLineRequestDto dto) {
        SupplierOrder order = findAndValidateOrder(dto.supplierOrderId());
        Article article = findAndValidateArticle(dto.articleId(), order.getId());
        
        SupplierOrderLine line = new SupplierOrderLine();
        line.setSupplierOrder(order);
        line.setArticle(article);
        line.setQuantity(dto.quantity());
        
        SupplierOrderLine savedLine = supplierOrderLineRepository.save(line);
        return supplierOrderLineMapper.toResponseDto(savedLine);
    }

    @Override
    public SupplierOrderLineResponseDto getLineById(Long id) {
        SupplierOrderLine line = supplierOrderLineRepository.findByIdWithArticle(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order line not found with id: " + id));
        return supplierOrderLineMapper.toResponseDto(line);
    }

    @Override
    public List<SupplierOrderLineResponseDto> getAllLinesForOrder(Long supplierOrderId) {
        SupplierOrder order = supplierOrderRepository.findByIdWithLines(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + supplierOrderId));
        return order.getSupplierOrderLineList().stream()
                .map(supplierOrderLineMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public SupplierOrderLineResponseDto update(Long id, SupplierOrderLineRequestDto dto) {
        SupplierOrderLine line = supplierOrderLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order line not found with id: " + id));
        
        validateOrderStateForUpdate(line.getSupplierOrder());
        
        // Valider le nouvel article si différent
        if (!line.getArticle().getId().equals(dto.articleId())) {
            Article newArticle = findAndValidateArticleForUpdate(dto.articleId(), line.getSupplierOrder().getId(), id);
            line.setArticle(newArticle);
        }
        
        line.setQuantity(dto.quantity());
        
        SupplierOrderLine updatedLine = supplierOrderLineRepository.save(line);
        return supplierOrderLineMapper.toResponseDto(updatedLine);
    }

    @Override
    @Transactional
    public SupplierOrderLineResponseDto updateLineQuantity(Long id, BigDecimal newQuantity) {
        SupplierOrderLine line = supplierOrderLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order line not found with id: " + id));
        
        validateOrderStateForUpdate(line.getSupplierOrder());
        
        line.setQuantity(newQuantity);
        SupplierOrderLine updatedLine = supplierOrderLineRepository.save(line);
        return supplierOrderLineMapper.toResponseDto(updatedLine);
    }

    @Override
    @Transactional
    public void removeLineFromOrder(Long id) {
        SupplierOrderLine line = supplierOrderLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order line not found with id: " + id));
        
        validateOrderStateForUpdate(line.getSupplierOrder());
        supplierOrderLineRepository.delete(line);
    }

    @Override
    public BigDecimal calculateOrderTotal(Long supplierOrderId) {
        SupplierOrder order = supplierOrderRepository.findById(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + supplierOrderId));
        
        return order.getSupplierOrderLineList().stream()
                .map(line -> line.getArticle().getUnitPriceAllTax().multiply(line.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isArticleAlreadyInOrder(Long supplierOrderId, Long articleId) {
        return supplierOrderLineRepository.existsBySupplierOrderIdAndArticleId(supplierOrderId, articleId);
    }

    @Override
    public List<SupplierOrderLineResponseDto> getAll() {
        return supplierOrderLineRepository.findAll()
                .stream()
                .map(supplierOrderLineMapper::toResponseDto)
                .toList();
    }
    
    private SupplierOrder findAndValidateOrder(Long supplierOrderId) {
        SupplierOrder order = supplierOrderRepository.findById(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + supplierOrderId));
        
        if (order.getStateOrder() != OrderStatus.PENDING) {
            throw new APIException("Cannot modify lines - order must be in PENDING status");
        }
        
        return order;
    }
    
    private Article findAndValidateArticle(Long articleId, Long orderId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        
        if (article.getStatus() == ArticleStatus.ARCHIVED) {
            throw new APIException("Cannot add an archived article to an order");
        }
        
        if (isArticleAlreadyInOrder(orderId, articleId)) {
            throw new APIException("This article is already present in the supplier order");
        }
        
        return article;
    }
    
    private void validateOrderStateForUpdate(SupplierOrder order) {
        if (order.getStateOrder() != OrderStatus.PENDING) {
            throw new APIException("Cannot modify lines - order must be in PENDING status");
        }
    }
    
    private Article findAndValidateArticleForUpdate(Long articleId, Long orderId, Long currentLineId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        
        if (article.getStatus() == ArticleStatus.ARCHIVED) {
            throw new APIException("Cannot use an archived article");
        }
        
        // Vérifier si l'article existe déjà dans une autre ligne de cette commande
        boolean articleExistsInOtherLine = supplierOrderLineRepository.findAll().stream()
                .anyMatch(line -> line.getSupplierOrder().getId().equals(orderId) 
                        && line.getArticle().getId().equals(articleId)
                        && !line.getId().equals(currentLineId));
        
        if (articleExistsInOtherLine) {
            throw new APIException("This article is already present in another line of this supplier order");
        }
        
        return article;
    }
}
