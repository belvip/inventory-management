package com.belvinard.inventory_management.dto.response;

import java.time.LocalDate;

public record ClientOrderResponseDto(
        Long id,
        String code,
        LocalDate orderDate,
        String comments,
        String stateOrder,
        LocalDate createdDate,
        LocalDate updatedDate
        //Long clientId
) {}

