package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.model.Article;
import com.belvinard.inventory_management.model.ClientOrder;
import com.belvinard.inventory_management.model.Sale;
import com.belvinard.inventory_management.model.SaleLine;
import com.belvinard.inventory_management.repository.SaleLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleLineService {

    private final SaleLineRepository saleLineRepository;

    public void generateFromOrder(Sale sale, ClientOrder order) {
        // Clear existing lines
        saleLineRepository.deleteAll(sale.getSaleLines());
        sale.getSaleLines().clear();
        
        order.getOrderClientLineList().forEach(orderLine -> {
            SaleLine saleLine = new SaleLine();
            Article article = orderLine.getArticle();
            saleLine.setSale(sale);
            saleLine.setArticle(article);
            saleLine.setQuantity(orderLine.getQuantity());
            saleLine.setUnitPriceExclTax(article.getUnitPriceExclTax());
            saleLine.setRateTva(article.getRateTva());
            saleLine.setUnitPriceAllTax(article.getUnitPriceAllTax());
            
            saleLineRepository.save(saleLine);
            sale.getSaleLines().add(saleLine);
        });
    }
}