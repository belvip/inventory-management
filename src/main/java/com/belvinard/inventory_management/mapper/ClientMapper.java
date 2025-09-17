package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.ClientRequestDto;
import com.belvinard.inventory_management.dto.response.ClientResponseDto;
import com.belvinard.inventory_management.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientResponseDto toResponseDto(Client client);
    Client toEntity(ClientRequestDto dto);
}
