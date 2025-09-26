package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
    Optional<SupplierOrder> findByCode(String code);
    
    @Query("SELECT so FROM SupplierOrder so LEFT JOIN FETCH so.supplierOrderLineList sol LEFT JOIN FETCH sol.article WHERE so.id = :id")
    Optional<SupplierOrder> findByIdWithLines(Long id);
}
