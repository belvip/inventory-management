package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.OrderClientLine;

import java.time.LocalDate;
import java.util.List;

public record ClientOrderResponseDto(
        Long id,
        String code,
        LocalDate orderDate,
        String comments,
        String stateOrder,
        List<OrderClientLine> orderClientLineList,
        LocalDate createdDate,
        LocalDate updatedDate
        //Long clientId
) {}

