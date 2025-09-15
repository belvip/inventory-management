package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.CategoryRequestDto;
import com.belvinard.inventory_management.dto.CategoryResponseDto;
import com.belvinard.inventory_management.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDto toResponseDto(Category category);
    Category toEntity(CategoryRequestDto dto);
}
