package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Entity -> ResponseDTO
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "role.roleName", target = "roleName")
    UserResponseDto toResponseDto(User user);

    // RequestDTO -> Entity
    User toEntity(UserRequestDto userRequestDto);
}
