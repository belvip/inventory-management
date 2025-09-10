package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto dto);

    void updateUserRole(Long userId, String roleName);

    UserResponseDto getUserById(Long id);

    UserResponseDto deleteUser(Long id);

    List<UserResponseDto> searchUser(String keyword);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(Long userId, UserRequestDto dto);
}
