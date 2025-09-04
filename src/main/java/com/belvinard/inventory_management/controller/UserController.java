package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.dto.UpdateUserRoleRequest;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    // ===========================================================
    // UPDATE ROLE
    // ===========================================================
    @Operation(
            summary = "Update a user's role",
            description = "Allows an ADMIN to update the role of an existing user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can access this endpoint", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public ResponseEntity<String> updateUserRole(@RequestBody @Valid UpdateUserRoleRequest request) {
        userService.updateUserRole(request.userId(), request.roleName());
        return ResponseEntity.ok("User role updated successfully");
    }

    // ===========================================================
    // CREATE USER
    // ===========================================================
    @Operation(
            summary = "Create a new user",
            description = "Allows an ADMIN to create a new user with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can access this endpoint", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflict - Username or email already exists", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    // ===========================================================
    // GET USER BY ID
    // ===========================================================
    @Operation(
            summary = "Get a user by ID",
            description = "Fetches a user by their unique ID. Accessible by ADMIN, SALES, or MANAGER.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Access denied", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    //@PreAuthorize("hasAnyRole('ADMIN','SALES','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ===========================================================
    // DELETE USER
    // ===========================================================
    @Operation(
            summary = "Delete a user",
            description = "Allows an ADMIN to delete a user by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can access this endpoint", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // ===========================================================
    // SEARCH USERS
    // ===========================================================
    @Operation(
            summary = "Search users by keyword",
            description = "Allows ADMIN, SALES, or MANAGER to search for users by username, email, or other fields.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid search keyword", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Access denied", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    //@PreAuthorize("hasAnyRole('ADMIN','SALES','MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUser(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUser(keyword));
    }

    // ===========================================================
    // GET ALL USERS
    // ===========================================================
    @Operation(
            summary = "Get all users",
            description = "Fetches all users in the system. Accessible by ADMIN, SALES, or MANAGER.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Access denied", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    //@PreAuthorize("hasAnyRole('ADMIN','SALES','MANAGER')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
