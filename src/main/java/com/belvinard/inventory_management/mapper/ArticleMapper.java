package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;
import com.belvinard.inventory_management.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.designation", target = "categoryDesignation")
    @Mapping(source = "availableQuantity", target = "availableQuantity")
    ArticleResponseDto toResponseDto(Article article);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reservedQuantity", ignore = true)
    Article toEntity(ArticleRequestDto dto);
}

