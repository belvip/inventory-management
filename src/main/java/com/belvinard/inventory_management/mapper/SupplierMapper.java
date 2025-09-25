package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.response.SupplierResponseDto;
import com.belvinard.inventory_management.model.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponseDto toResponseDto(Supplier supplier);
    Supplier toEntity(SupplierResponseDto dto);
}
