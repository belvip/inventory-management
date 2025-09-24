package com.belvinard.inventory_management.model;

import com.belvinard.inventory_management.validation.FutureOrPresent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Sale extends BaseEntity{
    
    @Column(unique = true, length = 50)
    private String code;

    @FutureOrPresent(message = "Order date cannot be in the past")
    @NotNull(message = "Sale date is mandatory")
    private LocalDate saleDate;

    @Size(max = 50, message = "Comment not exceed 50 characters")
    private String comments;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus saleStatus;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleLine> saleLines = new ArrayList<>();

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }

    @PrePersist
    @PreUpdate
    private void generateCode() {
        if (this.code == null && this.saleStatus == SaleStatus.CONFIRMED) {
            String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            this.code = "SALE-" + datePrefix + "-" + System.currentTimeMillis() % 10000;
        }
    }
}