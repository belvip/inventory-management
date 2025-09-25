package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.ClientRequestDto;
import com.belvinard.inventory_management.dto.response.ClientResponseDto;
import com.belvinard.inventory_management.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", uses = {ClientOrderMapper.class, SaleMapper.class},
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ClientMapper {

    @Mapping(source = "clientOrders", target = "orders")
    ClientResponseDto toResponseDto(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientOrders", ignore = true)
    Client toEntity(ClientRequestDto dto);
}
