package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SupplierOrderRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderResponseDto;
import com.belvinard.inventory_management.model.SupplierOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SupplierOrderLineMapper.class})
public interface SupplierOrderMapper {
   @Mapping(source = "supplierOrderLineList", target = "supplierOrderLineList")
   @Mapping(source = "supplier.id", target = "supplierId")
   SupplierOrderResponseDto toResponseDto(SupplierOrder supplierOrder);
   
   @Mapping(target = "supplier", ignore = true)
   @Mapping(target = "supplierOrderLineList", ignore = true)
   SupplierOrder toEntity(SupplierOrderRequestDto dto);
}
