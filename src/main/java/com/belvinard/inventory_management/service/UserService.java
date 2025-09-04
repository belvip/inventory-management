package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto dto);
}
