package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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

    @Pattern(
            regexp = "^[^\\s]+(?i)\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "The filename must end with a valid image extension (jpg, jpeg, png, gif, webp, bmp)."
    )
    private String image;

    @NotBlank(message = "The email is required")
    @Email(message = "The email must be valid")
    private String email;

    @NotBlank(message = "The phone number is required")
    @Size(min = 9, max = 20, message = "The phone number must contain between 9 and 20 characters")
    private String phoneNumber;

    @Size(max = 150, message = "The website must contain a maximum of 150 characters")
    private String website;

    @OneToMany(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Category> categories = new ArrayList<>();

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }
}
