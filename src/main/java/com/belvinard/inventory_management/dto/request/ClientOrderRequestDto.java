package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.belvinard.inventory_management.model.OrderStatus;

public record ClientOrderRequestDto(
        @NotBlank(message = "Order code is mandatory")
        @Size(min = 4, max = 50, message = "Order code must be between 4 and 50 characters")
        @Pattern(
                regexp = "ORD-\\d{3}|ORD[A-Z]{3}",
                message = "The code must be in the format ORD-XXX (e.g. ORD-123) or ORDABC"
        )
        String code,

        LocalDate orderDate,

        @NotNull(message = "Client ID is mandatory")
        Long clientId,

        @Size(min = 4, max = 255, message = "Comments must be between 4 and 255 characters")
        String comments,

        @NotNull(message = "Order status is mandatory")
        OrderStatus stateOrder
) {
}
