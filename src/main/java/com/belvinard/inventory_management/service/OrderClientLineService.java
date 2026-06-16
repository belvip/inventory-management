package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.model.OrderClientLine;
import com.belvinard.inventory_management.dto.request.OrderClientLineRequestDto;
import com.belvinard.inventory_management.dto.response.OrderClientLineResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface OrderClientLineService {

    OrderClientLineResponseDto addLineToOrder(OrderClientLineRequestDto Dto);
    OrderClientLineResponseDto getLineById(Long id);
    List<OrderClientLineResponseDto> getAllLinesForOrder(Long clientOrderId);
    OrderClientLineResponseDto updateLineQuantity(Long id, BigDecimal newQuantity);
    void removeLineFromOrder(Long id);
    List<OrderClientLineResponseDto> getAllLines();

    // Utility methods
    BigDecimal calculateOrderTotal(Long clientOrderId);
    boolean isArticleAlreadyInOrder(Long clientOrderId, Long articleId);
}