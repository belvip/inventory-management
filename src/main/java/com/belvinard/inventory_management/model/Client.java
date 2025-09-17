package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client extends BaseEntity {

    @NotBlank(message = "The client name is required")
    @Size(min = 4, max = 100, message = "The name must contain between 4 and 100 characters")
    @Column(nullable = false, length = 100) // ✅ Adds DB-level constraint
    private String name;

    @Embedded
    private Address address;

    @Email(message = "The email must be valid")
    @Column(unique = true, nullable = false) // ✅ Prevents duplicates
    private String email;

    @NotBlank(message = "The phone number is required")
    @Pattern(
            regexp = "^(?:(?:\\+237|237)[-.\\s]?)?(?:(?:[67][25-9]\\d{7})|(?:2\\d{2}\\d{6}))$",
            message = "The phone number must be a valid Cameroonian number (mobile or fixed). Examples: 671234567, 222123456, +237-233123456"
    )
    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClientOrder> clientOrders = new ArrayList<>();

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }
}
