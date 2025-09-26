package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.StockMovement;
import com.belvinard.inventory_management.model.StockMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByMovementType(StockMovementType movementType, Pageable pageable);
    List<StockMovement> findByArticleIdAndMovementType(Long articleId, StockMovementType movementType);
}