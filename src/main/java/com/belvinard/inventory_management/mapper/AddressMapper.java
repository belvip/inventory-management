package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.AddressDto;
import com.belvinard.inventory_management.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    AddressDto toDto(Address address);
    Address toEntity(AddressDto addressDto);
}
