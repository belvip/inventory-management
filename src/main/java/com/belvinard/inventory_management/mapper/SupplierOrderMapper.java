package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SupplierOrderRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderResponseDto;
import com.belvinard.inventory_management.model.SupplierOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierOrderMapper {
   SupplierOrderResponseDto toResponseDto(SupplierOrder supplierOrder);
   
   @Mapping(target = "supplier", ignore = true)
   SupplierOrder toEntity(SupplierOrderRequestDto dto);
}
