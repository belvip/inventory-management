package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.Sale;
import com.belvinard.inventory_management.model.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    boolean existsByClientIdAndSaleStatus(Long clientId, SaleStatus saleStatus);
}
