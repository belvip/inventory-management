package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type of stock movement")
public enum StockMovementType {
    @Schema(description = "Stock entry - increases inventory")
    IN,
    
    @Schema(description = "Stock exit - decreases inventory")
    OUT
}
