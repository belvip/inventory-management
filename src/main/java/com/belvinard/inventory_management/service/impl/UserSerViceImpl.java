package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.mapper.UserMapper;
import com.belvinard.inventory_management.model.AppRole;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.repository.UserRepository;
import com.belvinard.inventory_management.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // needed for updateUserRole
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // =====================================
    // CREATE USER
    // =====================================
    @Override
    public UserResponseDto createUser(UserRequestDto dto) {
        // Validation: email and username must be unique
        if (userRepository.existsByEmail(dto.email())) {
            throw new DataIntegrityViolationException("Email already exists: " + dto.email());
        }
        if (userRepository.existsByUserName(dto.userName())) {
            throw new DataIntegrityViolationException("Username already exists: " + dto.userName());
        }

        // Map DTO -> Entity
        User user = userMapper.toEntity(dto);

        // Encode password
        user.setPassword(passwordEncoder.encode(dto.password()));

        // Save user
        User saved = userRepository.save(user);

        return userMapper.toResponseDto(saved);
    }

    // =====================================
    // UPDATE USER ROLE
    // =====================================
    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByRoleName(AppRole.valueOf(roleName))
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));

        user.setRole(role);
        userRepository.save(user);
    }

    // =====================================
    // GET USER BY ID
    // =====================================
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    // =====================================
    // DELETE USER
    // =====================================
    @Override
    public UserResponseDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        return userMapper.toResponseDto(user);
    }

    // =====================================
    // SEARCH USER
    // =====================================
    @Override
    public List<User> searchUser(String keyword) {
        return userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    // =====================================
    // GET ALL USERS
    // =====================================
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    // =====================================
    // UPDATE USER
    // =====================================
    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Update fields
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setUserName(dto.userName());
        user.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        // Update address if present
        if (dto.address() != null) {
            user.setAddress(dto.address());
        }

        User updated = userRepository.save(user);
        return userMapper.toResponseDto(updated);
    }
}
