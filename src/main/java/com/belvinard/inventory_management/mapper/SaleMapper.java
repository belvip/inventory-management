package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;
import com.belvinard.inventory_management.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SaleLineMapper.class})
public interface SaleMapper {
    
    @Mapping(source = "saleStatus", target = "status")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "clientOrder.id", target = "clientOrderId")
    @Mapping(source = "clientOrder.code", target = "clientOrderCode")
    @Mapping(source = "saleLines", target = "saleLines")
    SaleResponseDto toResponseDto(Sale sale);
    
    @Mapping(source = "status", target = "saleStatus")
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "clientOrder", ignore = true)
    @Mapping(target = "code", ignore = true)
    Sale toEntity(SaleRequestDto dto);
}
