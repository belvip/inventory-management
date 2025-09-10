package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.AddressDto;
import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;

import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.mapper.AddressMapper;
import com.belvinard.inventory_management.mapper.UserMapper;
import com.belvinard.inventory_management.model.Address;
import com.belvinard.inventory_management.model.AppRole;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.repository.UserRepository;
import com.belvinard.inventory_management.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(@Valid UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ResourceConflictException("Email already exists: " + dto.email());
        }
        if (userRepository.existsByUserName(dto.userName())) {
            throw new ResourceConflictException("Username already exists: " + dto.userName());
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Role defaultRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role not configured"));
        user.setRole(defaultRole);

        if (dto.address() != null) {
            Address address = addressMapper.toEntity(dto.address());
            user.setAddress(address);
        }

        User saved = userRepository.save(user);
        return createResponseDto(saved);
    }
    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByRoleName(AppRole.valueOf(roleName.trim()))
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return createResponseDto(user);
    }

    @Override
    public UserResponseDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        return createResponseDto(user);
    }

    @Override
    public List<UserResponseDto> searchUser(String keyword) {
        return userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword, keyword, keyword)
                .stream()
                .map(this::createResponseDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::createResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setUserName(dto.userName());
        user.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        if (dto.address() != null) {
            Address address = new Address(
                dto.address().address1(),
                dto.address().address2(),
                dto.address().city(),
                dto.address().postalCode(),
                dto.address().country()
            );
            user.setAddress(address);
        }

        User updated = userRepository.save(user);
        return createResponseDto(updated);
    }

    private UserResponseDto createResponseDto(User user) {
        UserResponseDto baseResponse = userMapper.toResponseDto(user);
        
        AddressDto addressDto = null;
        if (user.getAddress() != null) {
            addressDto = new AddressDto(
                user.getAddress().getAddress1(),
                user.getAddress().getAddress2(),
                user.getAddress().getCity(),
                user.getAddress().getPostalCode(),
                user.getAddress().getCountry()
            );
        }

        return new UserResponseDto(
            baseResponse.userId(),
            baseResponse.firstName(),
            baseResponse.lastName(),
            baseResponse.userName(),
            baseResponse.email(),
            baseResponse.enabled(),
            baseResponse.accountNonLocked(),
            baseResponse.accountNonExpired(),
            baseResponse.credentialsNonExpired(),
            baseResponse.roleName(),
            baseResponse.image(),
            addressDto
        );
    }
}