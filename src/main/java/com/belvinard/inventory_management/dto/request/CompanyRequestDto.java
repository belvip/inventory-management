package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CompanyRequestDto(
        @NotBlank(message = "The company name is required")
        @Size(min = 4, max = 100, message = "The name must contain between 4 and 100 characters")
        String name,

        @NotBlank(message = "The description is required")
        @Size(min = 5, max = 200, message = "The description must contain between 5 and 200 characters")
        String description,

        Address address,

        @Size(min = 0, max = 20, message = "The fiscal code must contain a maximum of 20 characters")
        String fiscalCode,

        String image,

        @NotBlank(message = "The email is required")
        @Email(message = "The email must be valid")
        String email,

        @NotBlank(message = "The phone number is required")
        @Pattern(
                regexp = "^(?:(?:\\+237|237)[-.\\s]?)?(?:(?:[67][25-9]\\d{7})|(?:2\\d{2}\\d{6}))$",
                message = "The phone number must be a valid Cameroonian number (mobile or fixed). Examples: 671234567, 222123456, +237-233123456"
        )
        String phoneNumber,

        @Size(max = 150, message = "The website must contain a maximum of 150 characters")
        String website,
        @Schema(hidden = true)
         List<CategoryRequestDto> categories
) {}
