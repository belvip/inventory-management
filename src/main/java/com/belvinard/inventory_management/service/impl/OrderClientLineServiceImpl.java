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

        ClientOrder order = clientOrderRepository.findById(dto.clientOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.clientOrderId()));

        if (order.getStateOrder() != OrderStatus.IN_PREPARATION) {
            throw new IllegalStateException("Cannot add line to an order that is not in preparation");
        }

        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + dto.articleId()));

        if (article.getStatus() != ArticleStatus.ACTIVE) {
            throw new IllegalStateException("Article is not sellable");
        }

        BigDecimal reserved = orderClientLineRepository
                .sumQuantityForArticle(article.getId())
                .orElse(BigDecimal.ZERO);

        if (dto.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (article.getQuantityInStock() - reserved.longValue() < dto.quantity().longValue()) {
            throw new IllegalStateException("Not enough stock available for article: " + article.getCodeArticle());
        }

        if (isArticleAlreadyInOrder(order.getId(), article.getId())) {
            throw new IllegalStateException("Article already exists in this order");
        }

        OrderClientLine line = new OrderClientLine();
        line.setClientOrder(order);
        line.setArticle(article);
        line.setQuantity(dto.quantity());

        OrderClientLine savedLine = orderClientLineRepository.save(line);

        if (!order.getOrderClientLineList().isEmpty() || savedLine != null) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Order line not found with id: " + id));

        ClientOrder order = line.getClientOrder();

        if (order.getStateOrder() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot remove line from delivered order");
        }

        Article article = line.getArticle();
        article.setQuantityInStock(article.getQuantityInStock() + line.getQuantity().longValue());
        articleRepository.save(article);

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
        return orderClientLineRepository.existsByClientOrderIdAndArticleId(clientOrderId, articleId);
    }
}
