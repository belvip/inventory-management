package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserMapper {

    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(source = "id", target = "userId")
    UserResponseDto toResponseDto(User user);

    User toEntity(UserRequestDto dto);
}
