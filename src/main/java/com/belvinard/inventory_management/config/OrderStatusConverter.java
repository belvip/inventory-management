package com.belvinard.inventory_management.config;

import com.belvinard.inventory_management.model.OrderStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusConverter implements Converter<String, OrderStatus> {
    
    @Override
    public OrderStatus convert(String source) {
        return OrderStatus.fromString(source);
    }
}