package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.dto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
        String lastName,

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 10, message = "Username must be between 3 and 10 characters")
        String userName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 50, message = "Email must not exceed 50 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
        String password,

        String image,

        @Valid
        AddressDto address

) {}
