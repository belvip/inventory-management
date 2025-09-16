package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ArticleRequestDto(
        @NotBlank(message = "Article code is mandatory")
        @Size(min = 4, max = 50, message = "Article code must be between 4 and 50 characters")
        @Pattern(
                regexp = "ART-\\w{3}|ARTEFT",
                message = "The code must be in the format CAT-XXX or ARTEFT"
        )
        String codeArticle,

        @NotBlank(message = "Designation is mandatory")
        @Size(min = 4, max = 100, message = "Designation must be between 4 and 100 characters")
        String designation,

        Long quantityInStock,

        @NotNull(message = "Unit price excluding tax is mandatory")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "Unit price excluding tax must be positive")
        BigDecimal unitPriceExclTax,


        @DecimalMin(value = "0.0", inclusive = true,
                message = "VAT rate cannot be negative")
        BigDecimal rateTva,

        String image

) {
}
