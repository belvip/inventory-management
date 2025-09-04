package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto dto);

    void updateUserRole(Long userId, String roleName);

    User getUserById(Long id);

    UserResponseDto deleteUser(Long id);

    List<User> searchUser(String keyword);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(Long userId, UserRequestDto dto);
}
