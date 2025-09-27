package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.OrderClientLineRequestDto;
import com.belvinard.inventory_management.dto.response.OrderClientLineResponseDto;
import com.belvinard.inventory_management.model.OrderClientLine;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderClientLineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientOrder", ignore = true)
    @Mapping(target = "article", ignore = true)
    OrderClientLine toEntity(OrderClientLineRequestDto dto);

    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.codeArticle", target = "articleCode")
    @Mapping(source = "article.designation", target = "articleDesignation")
    @Mapping(source = "article.unitPriceExclTax", target = "unitPriceExclTax")
    @Mapping(source = "article.rateTva", target = "rateTva")
    @Mapping(source = "article.unitPriceAllTax", target = "unitPriceAllTax")
    @Mapping(target = "totalLinePrice", expression = "java(orderClientLine.getArticle().getUnitPriceAllTax().multiply(orderClientLine.getQuantity()))")
    OrderClientLineResponseDto toResponseDto(OrderClientLine orderClientLine);
}
