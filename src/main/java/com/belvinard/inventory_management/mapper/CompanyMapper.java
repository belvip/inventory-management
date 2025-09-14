package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.CompanyRequestDto;
import com.belvinard.inventory_management.dto.CompanyResponseDto;
import com.belvinard.inventory_management.model.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CompanyMapper {
    CompanyResponseDto toResponseDto(Company company);
    Company toEntity(CompanyRequestDto dto);
}
