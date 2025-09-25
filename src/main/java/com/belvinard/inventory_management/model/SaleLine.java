package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SaleLine extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @NotNull(message = "Quantity is mandatory")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    // Prix figés au moment de la vente
    @NotNull(message = "Unit price excluding tax is mandatory")
    private BigDecimal unitPriceExclTax;

    @NotNull(message = "VAT rate is mandatory")
    private BigDecimal rateTva;

    @NotNull(message = "Unit price including tax is mandatory")
    private BigDecimal unitPriceAllTax;

    @NotNull(message = "Total line price is mandatory")
    private BigDecimal totalLinePrice;

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }

    @PrePersist
    @PreUpdate
    private void calculateTotalPrice() {
        if (quantity != null && unitPriceAllTax != null) {
            this.totalLinePrice = quantity.multiply(unitPriceAllTax);
        }
    }
}