package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.MvtOrigin;
import com.belvinard.inventory_management.model.StockMovement;
import com.belvinard.inventory_management.model.StockMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByMovementType(StockMovementType movementType, Pageable pageable);
    List<StockMovement> findByArticleIdAndMovementType(Long articleId, StockMovementType movementType);
    List<StockMovement> findByArticleId(Long articleId);
    Page<StockMovement> findByMvtOrigin(MvtOrigin mvtOrigin, Pageable pageable);
    
    @Query("SELECT SUM(sm.quantity) FROM StockMovement sm WHERE sm.article.id = :articleId AND sm.movementType = :movementType")
    Long sumQuantityByArticleIdAndMovementType(@Param("articleId") Long articleId, @Param("movementType") StockMovementType movementType);
    
    List<StockMovement> findTopByOrderByCreatedDateDesc(Pageable pageable);
}