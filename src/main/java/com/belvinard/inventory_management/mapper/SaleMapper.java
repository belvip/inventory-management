package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;
import com.belvinard.inventory_management.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    
    @Mapping(source = "saleStatus", target = "status")
    @Mapping(source = "client.name", target = "clientName")
    SaleResponseDto toResponseDto(Sale sale);
    
    @Mapping(source = "status", target = "saleStatus")
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "code", ignore = true)
    Sale toEntity(SaleRequestDto dto);
}
