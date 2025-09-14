package com.belvinard.inventory_management.dto;

import com.belvinard.inventory_management.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequestDto(
        @NotBlank(message = "The company name is required")
        @Size(min = 4, max = 100, message = "The name must contain between 4 and 100 characters")
        String name,

        @NotBlank(message = "The description is required")
        @Size(min = 5, max = 200, message = "The description must contain between 5 and 200 characters")
        String description,

        Address address,

        @NotBlank(message = "The fiscal code is required")
        @Size(min = 5, max = 20, message = "The fiscal code must contain between 5 and 20 characters")
        String fiscalCode,

        String image,

        @NotBlank(message = "The email is required")
        @Email(message = "The email must be valid")
        String email,

        @NotBlank(message = "The phone number is required")
        @Size(min = 9, max = 20, message = "The phone number must contain between 9 and 20 characters")
        String phoneNumber,

        @Size(max = 150, message = "The website must contain a maximum of 150 characters")
        String website
) {}
