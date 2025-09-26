package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.Supplier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByName(String name);

    Optional<Supplier> findByName(String name);
}
