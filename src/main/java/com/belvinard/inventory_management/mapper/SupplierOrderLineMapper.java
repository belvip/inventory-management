package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SupplierOrderLineRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderLineResponseDto;
import com.belvinard.inventory_management.model.SupplierOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierOrderLineMapper {
    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.codeArticle", target = "articleCode")
    @Mapping(source = "article.designation", target = "articleDesignation")
    @Mapping(source = "article.unitPriceExclTax", target = "unitPriceExclTax")
    @Mapping(source = "article.rateTva", target = "rateTva")
    @Mapping(source = "article.unitPriceAllTax", target = "unitPriceAllTax")
    @Mapping(target = "totalLinePrice", expression = "java(calculateTotalLinePrice(supplierOrderLine))")
    SupplierOrderLineResponseDto toResponseDto(SupplierOrderLine supplierOrderLine);
    
    default java.math.BigDecimal calculateTotalLinePrice(SupplierOrderLine line) {
        if (line.getArticle() != null && line.getArticle().getUnitPriceAllTax() != null && line.getQuantity() != null) {
            return line.getQuantity().multiply(line.getArticle().getUnitPriceAllTax());
        }
        return java.math.BigDecimal.ZERO;
    }
    
    @Mapping(target = "supplierOrder", ignore = true)
    @Mapping(target = "article", ignore = true)
    SupplierOrderLine toEntity(SupplierOrderLineRequestDto dto);
}
