package com.belvinard.inventory_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @NotBlank(message = "The category designation is required")
    @Size(min = 2, max = 100, message = "The designation must contain between 2 and 100 characters")
    private String designation;

    @NotBlank(message = "The category code is required")
    @Size(min = 3, max = 10, message = "The code must contain between 3 and 10 characters")
    @Pattern(
            regexp = "CAT-\\w{3}|CATEFT",
            message = "The code must be in the format CAT-XXX or CATEFT"
    )
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    @Schema(hidden = true)
    public String getCreatedAt() {
        return getCreatedDate() != null ? getCreatedDate().toString() : null;
    }

    @Schema(hidden = true)
    public String getUpdatedAt() {
        return getUpdatedDate() != null ? getUpdatedDate().toString() : null;
    }
}
