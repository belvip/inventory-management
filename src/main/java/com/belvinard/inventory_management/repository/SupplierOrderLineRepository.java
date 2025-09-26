package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.SupplierOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierOrderLineRepository extends JpaRepository<SupplierOrderLine, Long> {
    boolean existsBySupplierOrderIdAndArticleId(Long supplierOrderId, Long articleId);
    
    @Query("SELECT sol FROM SupplierOrderLine sol JOIN FETCH sol.article WHERE sol.id = :id")
    Optional<SupplierOrderLine> findByIdWithArticle(Long id);
}