package com.belvinard.inventory_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final OrderStatusConverter orderStatusConverter;
    private final ArticleStatusConverter articleStatusConverter;

    public WebConfig(OrderStatusConverter orderStatusConverter, ArticleStatusConverter articleStatusConverter) {
        this.orderStatusConverter = orderStatusConverter;
        this.articleStatusConverter = articleStatusConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(orderStatusConverter);
        registry.addConverter(articleStatusConverter);
    }
}