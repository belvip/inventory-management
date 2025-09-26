package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SupplierRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierResponseDto;
import com.belvinard.inventory_management.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponseDto toResponseDto(Supplier supplier);
    
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "supplierOrders", ignore = true)
    Supplier toEntity(SupplierRequestDto dto);
}
