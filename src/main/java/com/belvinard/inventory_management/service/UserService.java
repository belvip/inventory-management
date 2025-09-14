package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.ResetPasswordRequest;
import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.model.User;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto dto);

    void updateUserRole(Long userId, String roleName);

    UserResponseDto getUserById(Long id);

    UserResponseDto deleteUser(Long id);

    List<UserResponseDto> searchUser(String keyword);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(Long userId, UserRequestDto dto);

    UserResponseDto updateUserImage(Long userId, MultipartFile image) throws IOException;

    String getPresignedImageUrl(Long id);

    UserResponseDto findByUsername(String username);
    
    void updatePasswordByUsername(String username, String password);

    void updateAccountLockStatus(Long userId, boolean lock);

    void updateCredentialsExpiryStatus(Long userId, boolean expire);

    void updateAccountEnabledStatus(Long userId, boolean enabled);

    void updateAccountExpiryStatus(Long userId, boolean expire);

    void generatePasswordResetToken(String email);

    void resetPassword(@Valid ResetPasswordRequest request);
}
