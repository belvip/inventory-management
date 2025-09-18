package com.belvinard.inventory_management.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Possible statuses of a client order")
public enum OrderStatus {
    @Schema(description = "Order is still being prepared")
    IN_PREPARATION,

    @Schema(description = "Order is validated and waiting to be delivered")
    VALIDATED,

    @Schema(description = "Order is delivered to the client")
    DELIVERED,

    @Schema(description = "Order was canceled")
    CANCELED;


    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static OrderStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderStatus value cannot be null or empty. Valid values are: in_preparation, validated, delivered, canceled");
        }

        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid OrderStatus: '" + value + "'. Valid values are: in_preparation, validated, delivered, canceled"
            );
        }
    }
}