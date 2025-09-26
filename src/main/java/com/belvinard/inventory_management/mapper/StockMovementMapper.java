package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.StockMovementRequestDto;
import com.belvinard.inventory_management.dto.response.StockMovementResponseDto;
import com.belvinard.inventory_management.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    
    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.codeArticle", target = "articleCode")
    @Mapping(source = "article.designation", target = "articleDesignation")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    StockMovementResponseDto toResponseDto(StockMovement stockMovement);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "article", ignore = true)
    StockMovement toEntity(StockMovementRequestDto dto);

    List<StockMovementResponseDto> toResponseDtoList(List<StockMovement> stockMovements);

    default Page<StockMovementResponseDto> toResponseDtoPage(Page<StockMovement> stockMovementPage) {
        return stockMovementPage.map(this::toResponseDto);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "article", ignore = true)
    void updateEntityFromDto(StockMovementRequestDto dto, @MappingTarget StockMovement stockMovement);
}