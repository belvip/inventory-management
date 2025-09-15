package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    @NotBlank(message = "The company name is required")
    @Size(min = 4, max = 100, message = "The name must contain between 4 and 100 characters")
    private String name;

    @NotBlank(message = "The description is required")
    @Size(min = 5, max = 200, message = "The description must contain between 5 and 200 characters")
    private String description;

    @Embedded
    private Address address;

    @NotBlank(message = "The fiscal code is required")
    @Size(min = 5, max = 20, message = "The fiscal code must contain between 5 and 20 characters")
    private String fiscalCode;

    private String image;

    @NotBlank(message = "The email is required")
    @Email(message = "The email must be valid")
    private String email;

    @NotBlank(message = "The phone number is required")
    @Size(min = 9, max = 20, message = "The phone number must contain between 9 and 20 characters")
    private String phoneNumber;

    @Size(max = 150, message = "The website must contain a maximum of 150 characters")
    private String website;

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }
}
