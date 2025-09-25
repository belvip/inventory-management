package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existByName(String name);
}
