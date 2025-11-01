package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "article")
public class Article extends BaseEntity {

    @NotBlank(message = "Article code is mandatory")
    @Size(min = 3, max = 50, message = "Article code must be between 3 and 50 characters")
    @Pattern(
            regexp = "ART-\\d{3}|ART[A-Z]{3}",
            message = "The code must be in the format ART-XXX or ARTEFT"
    )
    @Column(nullable = false, unique = true, length = 50)
    private String codeArticle;

    @NotBlank(message = "Designation is mandatory")
    @Size(min = 4, max = 100, message = "Designation must be between 4 and 100 characters")
    private String designation;

    private Long quantityInStock;

    @Column(nullable = false)
    private Long reservedQuantity = 0L;

    @NotNull(message = "Unit price excluding tax is mandatory")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Unit price excluding tax must be positive")
    private BigDecimal unitPriceExclTax ;

    @DecimalMin(value = "0.0", inclusive = true,
            message = "VAT rate cannot be negative")
    private BigDecimal rateTva;

    @Schema(hidden = true)
    private BigDecimal unitPriceAllTax;

    @Pattern(
            regexp = "^[^\\s]+(?i)\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "The filename must end with a valid image extension (jpg, jpeg, png, gif, webp, bmp)."
    )
    private String image;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleStatus status = ArticleStatus.ACTIVE;

    public void archive() {
        this.status = ArticleStatus.ARCHIVED;
    }

    public void restore() {
        this.status = ArticleStatus.ACTIVE;
    }

    public Long getAvailableQuantity() {
        return quantityInStock - reservedQuantity;
    }

    public void reserveQuantity(Long quantity) {
        this.reservedQuantity += quantity;
    }

    public void releaseReservedQuantity(Long quantity) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }

    public void finalizeStock(Long quantity) {
        this.quantityInStock -= quantity;
        this.reservedQuantity -= quantity;
    }

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
    public void calculateUnitPriceAllTax() {
        if (unitPriceExclTax != null && rateTva != null) {
            this.unitPriceAllTax = unitPriceExclTax.add(
                    unitPriceExclTax.multiply(rateTva.divide(BigDecimal.valueOf(100)))
            );
        }
    }

}
