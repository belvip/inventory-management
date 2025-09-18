package com.belvinard.inventory_management.config;

import com.belvinard.inventory_management.model.ArticleStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArticleStatusConverter implements Converter<String, ArticleStatus> {
    
    @Override
    public ArticleStatus convert(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("ArticleStatus value cannot be null or empty. Valid values are: active, archived");
        }

        try {
            return ArticleStatus.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid ArticleStatus: '" + source + "'. Valid values are: active, archived"
            );
        }
    }
}