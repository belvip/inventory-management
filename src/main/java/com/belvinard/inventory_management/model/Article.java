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
    @Size(min = 4, max = 50, message = "Article code must be between 4 and 50 characters")
    @Pattern(
            regexp = "ART-\\w{3}|ARTEFT",
            message = "The code must be in the format ART-XXX or ARTEFT"
    )
    @Column(nullable = false, unique = true, length = 50)
    private String codeArticle;

    @NotBlank(message = "Designation is mandatory")
    @Size(min = 4, max = 100, message = "Designation must be between 4 and 100 characters")
    private String designation;

    private Long quantityInStock;

    @NotNull(message = "Unit price excluding tax is mandatory")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Unit price excluding tax must be positive")
    private BigDecimal unitPriceExclTax ;

    @DecimalMin(value = "0.0", inclusive = true,
            message = "VAT rate cannot be negative")
    private BigDecimal rateTva;

    @Schema(hidden = true)
    private BigDecimal unitPriceAllTax;

    private String image;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }
}
