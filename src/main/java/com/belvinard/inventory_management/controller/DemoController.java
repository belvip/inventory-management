package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.response.UserResponseDto;
import com.belvinard.inventory_management.mapper.UserMapper;
import com.belvinard.inventory_management.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Demo Users", description = "Demo endpoints for testing. PUBLIC ACCESS - No authentication required.")
public class DemoController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    @Operation(summary = "Get all demo users [PUBLIC ACCESS]", description = "Returns information about all demo users available for testing")
    public ResponseEntity<List<UserResponseDto>> getAllDemoUsers() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }


}