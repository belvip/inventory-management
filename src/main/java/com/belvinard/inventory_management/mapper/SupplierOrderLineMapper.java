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
    @Mapping(expression = "java(supplierOrderLine.getQuantity().multiply(supplierOrderLine.getArticle().getUnitPriceAllTax()))", target = "totalLinePrice")
    SupplierOrderLineResponseDto toResponseDto(SupplierOrderLine supplierOrderLine);
    
    @Mapping(target = "supplierOrder", ignore = true)
    @Mapping(target = "article", ignore = true)
    SupplierOrderLine toEntity(SupplierOrderLineRequestDto dto);
}
