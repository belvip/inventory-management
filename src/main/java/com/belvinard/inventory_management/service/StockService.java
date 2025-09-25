package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.model.Article;
import com.belvinard.inventory_management.model.SaleLine;
import org.springframework.stereotype.Component;

@Component
public class StockService {

    public void consumeStock(SaleLine saleLine) {
        Article article = saleLine.getArticle();
        Long qty = saleLine.getQuantity().longValue();
        if (article.getQuantityInStock() < qty) {
            throw new APIException("Insufficient stock for article: " + article.getDesignation());
        }
        article.setQuantityInStock(article.getQuantityInStock() - qty);
        article.releaseReservedQuantity(qty);
    }
}