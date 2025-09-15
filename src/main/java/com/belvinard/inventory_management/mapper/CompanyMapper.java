package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.CompanyRequestDto;
import com.belvinard.inventory_management.dto.CompanyResponseDto;
import com.belvinard.inventory_management.model.Company;
import org.mapstruct.Mapper;

// This annotation tells MapStruct to generate an implementation for this interface
// and register it as a Spring Bean, so you can @Autowired it in your services.
@Mapper(componentModel = "spring", uses = {AddressMapper.class, CategoryMapper.class})
public interface CompanyMapper {

    /**
     * Converts a Company entity to a CompanyResponseDto.
     *
     * - The "entity" is your JPA object stored in the database.
     * - The "DTO" is the object you expose via your API.
     *
     * MapStruct automatically maps fields with the same name.
     * For complex types like Address, it uses the AddressMapper (specified in `uses`).
     */
    CompanyResponseDto toResponseDto(Company company);

    /**
     * Converts a CompanyRequestDto to a Company entity.
     *
     * - The "request DTO" is the object received from the client when creating or updating a company.
     * - MapStruct automatically maps matching fields from the DTO to the entity.
     * - Nested objects like Address will be mapped using AddressMapper.
     */
    Company toEntity(CompanyRequestDto dto);
}
