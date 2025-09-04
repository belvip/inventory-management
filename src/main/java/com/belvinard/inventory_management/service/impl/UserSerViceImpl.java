package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.mapper.UserMapper;
import com.belvinard.inventory_management.repository.UserRepository;
import com.belvinard.inventory_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSerViceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto dto) {
        return null;
    }
}
