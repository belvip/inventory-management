package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.AddressDto;
import com.belvinard.inventory_management.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);
    Address toEntity(AddressDto addressDto);
}
