package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.Article;
import com.belvinard.inventory_management.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByCodeArticle(String s);

    List<Article> findByStatus(ArticleStatus articleStatus);


    @Query("SELECT COUNT(a) FROM Article a WHERE a.quantityInStock <= :threshold")
    Long countByQuantityInStockLessThanEqual(@Param("threshold") Long threshold);
}
