package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.model.ClientOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ClientOrderMapper {

    @Mapping(source = "orderDate", target = "orderDate")
    @Mapping(source = "stateOrder", target = "stateOrder")
    //@Mapping(source = "client.id", target = "clientId")
    ClientOrderResponseDto toResponseDto(ClientOrder clientOrder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "client", ignore = true)
    ClientOrder toEntity(ClientOrderRequestDto dto);
}
