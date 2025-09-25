package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.OrderClientLineRequestDto;
import com.belvinard.inventory_management.dto.response.OrderClientLineResponseDto;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.OrderClientLineMapper;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.ArticleRepository;
import com.belvinard.inventory_management.repository.ClientOrderRepository;
import com.belvinard.inventory_management.repository.OrderClientLineRepository;
import com.belvinard.inventory_management.service.OrderClientLineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderClientLineServiceImpl implements OrderClientLineService {

    private static final String ORDER_NOT_FOUND_MSG = "Order not found with id: ";
    private static final String ORDER_LINE_NOT_FOUND_MSG = "Order line not found";
    private static final String ARTICLE_NOT_FOUND_MSG = "Article not found with id: ";

    private final OrderClientLineRepository orderClientLineRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final ArticleRepository articleRepository;
    private final OrderClientLineMapper orderClientLineMapper;

    @Override
    @Transactional
    public OrderClientLineResponseDto addLineToOrder(OrderClientLineRequestDto dto) {
        validateRequest(dto);
        
        ClientOrder order = findAndValidateOrder(dto.clientOrderId());
        Article article = findAndValidateArticle(dto.articleId(), order.getId());
        
        reserveStock(article, dto.quantity().longValue());
        
        OrderClientLine savedLine = createAndSaveOrderLine(order, article, dto.quantity());
        
        return orderClientLineMapper.toResponseDto(savedLine);
    }
    
    private void validateRequest(OrderClientLineRequestDto dto) {
        if (dto == null) {
            throw new APIException("Order line request cannot be null");
        }
        if (dto.clientOrderId() == null) {
            throw new APIException("Client order ID is required");
        }
        if (dto.articleId() == null) {
            throw new APIException("Article ID is required");
        }
    }
    
    private ClientOrder findAndValidateOrder(Long clientOrderId) {
        ClientOrder order = clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + clientOrderId));
        
        if (order.getStateOrder() != OrderStatus.PENDING) {
            throw new APIException("Cannot add line. Order is not in PENDING state.");
        }
        
        return order;
    }
    
    private Article findAndValidateArticle(Long articleId, Long orderId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_NOT_FOUND_MSG + articleId));
        
        if (article.getStatus() == ArticleStatus.ARCHIVED) {
            throw new APIException("Cannot add an archived article to an order");
        }
        
        if (isArticleAlreadyInOrder(orderId, articleId)) {
            throw new APIException("This article is already present in the order.");
        }
        
        return article;
    }
    
    private void reserveStock(Article article, Long requestedQuantity) {
        Long availableQuantity = article.getAvailableQuantity();
        if (requestedQuantity > availableQuantity) {
            throw new APIException("Insufficient stock. Available: " + availableQuantity + ", Requested: " + requestedQuantity);
        }
        
        article.reserveQuantity(requestedQuantity);
        articleRepository.save(article);
    }
    
    private OrderClientLine createAndSaveOrderLine(ClientOrder order, Article article, BigDecimal quantity) {
        OrderClientLine line = new OrderClientLine();
        line.setClientOrder(order);
        line.setArticle(article);
        line.setQuantity(quantity);
        
        return orderClientLineRepository.save(line);
    }


    @Override
    public OrderClientLineResponseDto getLineById(Long id) {
        OrderClientLine line = orderClientLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order line not found with id: " + id));
        return orderClientLineMapper.toResponseDto(line);
    }

    @Override
    public List<OrderClientLineResponseDto> getAllLinesForOrder(Long clientOrderId) {
        ClientOrder order = clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + clientOrderId));
        return order.getOrderClientLineList().stream()
                .map(orderClientLineMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderClientLineResponseDto updateLineQuantity(Long id, BigDecimal newQuantity) {
        OrderClientLine line = findAndValidateOrderLineForUpdate(id);
        
        BigDecimal delta = calculateQuantityDelta(line.getQuantity(), newQuantity);
        updateReservation(line.getArticle(), delta);
        
        OrderClientLine updatedLine = updateAndSaveOrderLine(line, newQuantity);
        
        return orderClientLineMapper.toResponseDto(updatedLine);
    }
    
    private OrderClientLine findAndValidateOrderLineForUpdate(Long id) {
        OrderClientLine line = orderClientLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_LINE_NOT_FOUND_MSG));
        
        validateOrderStateForUpdate(line.getClientOrder());
        validateArticleForUpdate(line.getArticle());
        
        return line;
    }
    
    private void validateOrderStateForUpdate(ClientOrder order) {
        if (order.getStateOrder() != OrderStatus.PENDING) {
            throw new APIException("Cannot update order line. Order is not in PENDING state.");
        }
    }
    
    private void validateArticleForUpdate(Article article) {
        if (article == null) {
            throw new APIException("This order line does not have a valid article associated.");
        }
    }
    
    private BigDecimal calculateQuantityDelta(BigDecimal currentQuantity, BigDecimal newQuantity) {
        return newQuantity.subtract(currentQuantity);
    }
    
    private void updateReservation(Article article, BigDecimal delta) {
        if (delta.compareTo(BigDecimal.ZERO) > 0) {
            handleQuantityIncrease(article, delta);
        } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
            handleQuantityDecrease(article, delta);
        }
        articleRepository.save(article);
    }
    
    private void handleQuantityIncrease(Article article, BigDecimal delta) {
        if (delta.longValue() > article.getAvailableQuantity()) {
            throw new APIException("Insufficient stock. Available: " + article.getAvailableQuantity() + ", Additional requested: " + delta);
        }
        article.reserveQuantity(delta.longValue());
    }
    
    private void handleQuantityDecrease(Article article, BigDecimal delta) {
        article.releaseReservedQuantity(Math.abs(delta.longValue()));
    }
    
    private OrderClientLine updateAndSaveOrderLine(OrderClientLine line, BigDecimal newQuantity) {
        line.setQuantity(newQuantity);
        return orderClientLineRepository.save(line);
    }

    @Override
    @Transactional
    public void removeLineFromOrder(Long id) {
        OrderClientLine line = orderClientLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_LINE_NOT_FOUND_MSG));

        // Libérer la réservation lors de la suppression de la ligne
        line.releaseReservation();
        articleRepository.save(line.getArticle());

        orderClientLineRepository.delete(line);
    }
    @Override
    public BigDecimal calculateOrderTotal(Long clientOrderId) {
        ClientOrder order = clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + clientOrderId));

        return order.getOrderClientLineList().stream()
                .map(l -> l.getArticle().getUnitPriceAllTax().multiply(l.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isArticleAlreadyInOrder(Long clientOrderId, Long articleId) {
        if (clientOrderId == null || articleId == null) {
            throw new APIException("ClientOrderId and ArticleId cannot be null");
        }
        return orderClientLineRepository.existsByClientOrderIdAndArticleId(clientOrderId, articleId);
    }
}
