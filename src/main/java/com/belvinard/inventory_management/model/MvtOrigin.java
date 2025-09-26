package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Origin of stock movement")
public enum MvtOrigin {
    @Schema(description = "Movement from sales/deliveries")
    SALES,
    
    @Schema(description = "Movement from client order processing")
    CLIENT_ORDER,
    
    @Schema(description = "Movement from supplier order reception")
    SUPPLIER_ORDER,
    
    @Schema(description = "Manual stock adjustment")
    MANUAL_ADJUSTMENT,
    
    @Schema(description = "Initial stock entry")
    INITIAL_STOCK
}
