package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SaleLineRequestDto;
import com.belvinard.inventory_management.dto.response.SaleLineResponseDto;
import com.belvinard.inventory_management.model.SaleLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleLineMapper {
    
    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.codeArticle", target = "articleCode")
    @Mapping(source = "article.designation", target = "articleDesignation")
    SaleLineResponseDto toResponseDto(SaleLine saleLine);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "sale", ignore = true)
    @Mapping(target = "article", ignore = true)
    @Mapping(target = "unitPriceExclTax", ignore = true)
    @Mapping(target = "rateTva", ignore = true)
    @Mapping(target = "unitPriceAllTax", ignore = true)
    @Mapping(target = "totalLinePrice", ignore = true)
    SaleLine toEntity(SaleLineRequestDto dto);
}