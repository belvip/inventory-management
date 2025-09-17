package com.belvinard.inventory_management.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum OrderStatus {
    IN_PREPARATION,
    VALIDATED,
    DELIVERED,
    CANCELED;


    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static OrderStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderStatus value cannot be null or empty.");
        }

        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Unknown OrderStatus: '" + value + "'. Valid values are: " + Arrays.toString(OrderStatus.values())
            );
        }
    }
}