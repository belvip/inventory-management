package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.UserResponseDto;
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
@Tag(name = "Demo Users", description = "Endpoints to retrieve demo user information for testing")
public class DemoController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    @Operation(summary = "Get all demo users", description = "Returns information about all demo users available for testing")
    public ResponseEntity<List<UserResponseDto>> getAllDemoUsers() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/admin")
    @Operation(summary = "Get admin user info", description = "Returns admin user information for testing")
    public ResponseEntity<UserResponseDto> getAdminUser() {
        return userRepository.findByUserName("admin")
                .map(userMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/manager")
    @Operation(summary = "Get manager user info", description = "Returns manager user information for testing")
    public ResponseEntity<UserResponseDto> getManagerUser() {
        return userRepository.findByUserName("manager")
                .map(userMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/sales")
    @Operation(summary = "Get sales user info", description = "Returns sales user information for testing")
    public ResponseEntity<UserResponseDto> getSalesUser() {
        return userRepository.findByUserName("sales")
                .map(userMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/user")
    @Operation(summary = "Get regular user info", description = "Returns regular user information for testing")
    public ResponseEntity<UserResponseDto> getRegularUser() {
        return userRepository.findByUserName("user")
                .map(userMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}