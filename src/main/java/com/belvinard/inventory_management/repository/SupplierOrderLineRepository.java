package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.SupplierOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierOrderLineRepository extends JpaRepository<SupplierOrderLine, Long> {
    boolean existsBySupplierOrderIdAndArticleId(Long supplierOrderId, Long articleId);
}