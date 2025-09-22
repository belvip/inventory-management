package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.OrderClientLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OrderClientLineRepository extends JpaRepository<OrderClientLine, Long> {
    
    @Query("SELECT SUM(ol.quantity) FROM OrderClientLine ol WHERE ol.article.id = :articleId")
    Optional<BigDecimal> sumQuantityForArticle(@Param("articleId") Long articleId);
    
    boolean existsByClientOrderIdAndArticleId(Long clientOrderId, Long articleId);
}
