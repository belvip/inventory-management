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

    private final OrderClientLineRepository orderClientLineRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final ArticleRepository articleRepository;
    private final OrderClientLineMapper orderClientLineMapper;

    @Override
    @Transactional
    public OrderClientLineResponseDto addLineToOrder(OrderClientLineRequestDto dto) {
        if (dto == null) {
            throw new APIException("Order line request cannot be null");
        }
        if (dto.clientOrderId() == null) {
            throw new APIException("Client order ID is required");
        }
        if (dto.articleId() == null) {
            throw new APIException("Article ID is required");
        }

        ClientOrder order = clientOrderRepository.findById(dto.clientOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.clientOrderId()));

        if (order.getStateOrder() != OrderStatus.IN_PREPARATION) {
            throw new APIException("Cannot add line. Order is not in IN_PREPARATION state.");
        }

        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + dto.articleId()));

        if (article.getStatus() == ArticleStatus.ARCHIVED) {
            throw new APIException("Cannot add an archived article to an order");
        }

        if (isArticleAlreadyInOrder(order.getId(), article.getId())) {
            throw new APIException("This article is already present in the order.");
        }

        long stock = article.getQuantityInStock() != null ? article.getQuantityInStock() : 0L;
        if (dto.quantity().longValue() > stock) {
            throw new APIException("Not enough stock available for this article.");
        }

        article.setQuantityInStock(stock - dto.quantity().longValue());
        articleRepository.save(article);

        OrderClientLine line = new OrderClientLine();
        line.setClientOrder(order);
        line.setArticle(article);
        line.setQuantity(dto.quantity());

        OrderClientLine savedLine = orderClientLineRepository.save(line);

        // 8️⃣ ✅ FIX: Only update order status if currently IN_PREPARATION and this was the FIRST line
        if (order.getStateOrder() == OrderStatus.IN_PREPARATION &&
                order.getOrderClientLineList().isEmpty()) {
            order.setStateOrder(OrderStatus.VALIDATED);
            clientOrderRepository.save(order);
        }

        return orderClientLineMapper.toResponseDto(savedLine);
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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + clientOrderId));
        return order.getOrderClientLineList().stream()
                .map(orderClientLineMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderClientLineResponseDto updateLineQuantity(Long id, BigDecimal newQuantity) {
        OrderClientLine line = orderClientLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order line not found"));

        ClientOrder order = line.getClientOrder();
        if (order.getStateOrder() != OrderStatus.IN_PREPARATION) {
            throw new APIException("Cannot update order line. Order is not in IN_PREPARATION state.");
        }

        Article article = line.getArticle();
        if (article == null) {
            throw new APIException("This order line does not have a valid article associated.");
        }

        long totalStock = article.getQuantityInStock() != null ? article.getQuantityInStock() : 0L;
        BigDecimal currentlyReserved = line.getQuantity();
        BigDecimal delta = newQuantity.subtract(currentlyReserved); // Change amount

        if (delta.compareTo(BigDecimal.ZERO) > 0 && delta.longValue() > totalStock) {
            throw new APIException("Not enough stock available. Requested additional quantity exceeds stock.");
        }

        article.setQuantityInStock(totalStock - delta.longValue());
        articleRepository.save(article);

        line.setQuantity(newQuantity);
        OrderClientLine updatedLine = orderClientLineRepository.save(line);

        return orderClientLineMapper.toResponseDto(updatedLine);
    }

    @Override
    @Transactional
    public void removeLineFromOrder(Long id) {
        OrderClientLine line = orderClientLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order line not found"));

        Article article = line.getArticle();
        if (article != null) {
            long currentStock = article.getQuantityInStock() != null ? article.getQuantityInStock() : 0L;
            long lineQuantity = line.getQuantity() != null ? line.getQuantity().longValue() : 0L;

            // ✅ Null-safe stock restoration
            article.setQuantityInStock(currentStock + lineQuantity);
            articleRepository.save(article);
        }

        orderClientLineRepository.delete(line);
    }
    @Override
    public BigDecimal calculateOrderTotal(Long clientOrderId) {
        ClientOrder order = clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + clientOrderId));

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
