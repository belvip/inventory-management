
package com.belvinard.inventory_management.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Possible statuses of a client order")
public enum OrderStatus {
    @Schema(description = "Order is pending validation")
    PENDING,

    @Schema(description = "Order is confirmed and ready for processing")
    CONFIRMED,

    @Schema(description = "Order was cancelled")
    CANCELLED,

    @Schema(description = "Order is completed/delivered")
    COMPLETED;


    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static OrderStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderStatus value cannot be null or empty. Valid values are: pending, confirmed, cancelled, completed");
        }

        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid OrderStatus: '" + value + "'. Valid values are: pending, confirmed, cancelled, completed"
            );
        }
    }
}
