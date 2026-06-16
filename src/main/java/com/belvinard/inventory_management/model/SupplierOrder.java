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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SupplierOrder extends BaseEntity{
    @NotBlank(message = "Order code is mandatory")
    @Size(min = 3, max = 50, message = "Order code must be between 3 and 50 characters")
    @Pattern(
            regexp = "ORD-\\d{3}|ORD[A-Z]{3}",
            message = "The code must be in the format ORD-XXX (e.g. ORD-123) or ORDABC"
    )
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    private LocalDate orderDate;

    @Size(min = 4, max = 255, message = "Comments must be between 4 and 255 characters")
    @Column(length = 255)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus stateOrder;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "supplierOrder", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    List<SupplierOrderLine> supplierOrderLineList = new ArrayList<>();

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }



}
