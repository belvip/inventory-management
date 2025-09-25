package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.model.Article;
import com.belvinard.inventory_management.model.ClientOrder;
import com.belvinard.inventory_management.model.Sale;
import com.belvinard.inventory_management.model.SaleLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleLineService {

    public void generateFromOrder(Sale sale, ClientOrder order) {
        order.getOrderClientLineList().forEach(orderLine -> {
            SaleLine saleLine = new SaleLine();
            Article article = orderLine.getArticle();
            saleLine.setSale(sale);
            saleLine.setArticle(article);
            saleLine.setQuantity(orderLine.getQuantity());
            saleLine.setUnitPriceExclTax(article.getUnitPriceExclTax());
            saleLine.setRateTva(article.getRateTva());
            saleLine.setUnitPriceAllTax(article.getUnitPriceAllTax());
            sale.getSaleLines().add(saleLine);
        });
    }
}