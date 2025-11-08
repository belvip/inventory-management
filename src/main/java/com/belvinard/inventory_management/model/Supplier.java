package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "suppliers")
public class Supplier extends BaseEntity{
    @NotBlank(message = "The company name is required")
    @Size(min = 3, max = 100, message = "The name must contain between 4 and 100 characters")
    private String name;

    @Pattern(
            regexp = "^$|^(?:(?:\\+237|237)[-.\\s]?)?(?:[67][25-9]\\d{7}|2\\d{2}\\d{6})$",
            message = "The phone number must be a valid Cameroonian number (mobile or fixed). Examples: 671234567, 222123456, +237-233123456"
    )
    private String phoneNumber;

    private String address;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupplierOrder> supplierOrders = new ArrayList<>();

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }


}
