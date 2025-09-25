package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Long> {
}
