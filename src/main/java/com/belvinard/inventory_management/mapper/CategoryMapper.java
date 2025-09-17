package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.CategoryRequestDto;
import com.belvinard.inventory_management.dto.response.CategoryResponseDto;
import com.belvinard.inventory_management.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", uses = ArticleMapper.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface CategoryMapper {
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    CategoryResponseDto toResponseDto(Category category);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "company", ignore = true)
    Category toEntity(CategoryRequestDto dto);
}
