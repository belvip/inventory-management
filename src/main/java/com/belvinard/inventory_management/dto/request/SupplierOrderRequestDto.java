package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.model.OrderStatus;
import com.belvinard.inventory_management.validation.FutureOrPresent;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SupplierOrderRequestDto(
        @NotBlank(message = "Order code is mandatory")
        @Size(min = 4, max = 50, message = "Order code must be between 4 and 50 characters")
        @Pattern(
                regexp = "ORD-\\d{3}|ORD[A-Z]{3}",
                message = "The code must be in the format ORD-XXX (e.g. ORD-123) or ORDABC"
        )
        @Column(nullable = false, unique = true, length = 50)
        String code,

        @FutureOrPresent(message = "Order date cannot be in th past")
        LocalDate orderDate,

        @NotNull(message = "Supplier ID is mandatory")
        Long supplierId,

        @Size(min = 4, max = 255, message = "Comments must be between 4 and 255 characters")
        String comments,

        @NotNull(message = "Order is mandatory")
        OrderStatus stateOrder


) {
}
