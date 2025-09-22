package com.belvinard.inventory_management.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ClientOrderResponseDto(
        Long id,
        String code,
        LocalDate orderDate,
        String comments,
        String stateOrder,
        Long clientId,
        List<OrderClientLineResponseDto> orderClientLineList,
        LocalDate createdDate,
        LocalDate updatedDate
) {}

