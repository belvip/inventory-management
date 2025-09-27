package com.belvinard.inventory_management.mapper;

import com.belvinard.inventory_management.dto.request.UserRequestDto;
import com.belvinard.inventory_management.dto.response.UserResponseDto;
import com.belvinard.inventory_management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AddressMapper.class})
public interface UserMapper {

    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    UserResponseDto toResponseDto(User user);

    User toEntity(UserRequestDto dto);
}
