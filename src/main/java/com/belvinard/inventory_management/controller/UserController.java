package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.*;
import com.belvinard.inventory_management.dto.response.UserResponseDto;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    // ===========================================================
    // CREATE USER
    // ===========================================================
    @Operation(
            summary = "Create a new user - Only admin",
            description = "Creates a new user with default role ROLE_USER. "
                    + "Fails if email or username already exist."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (Email or Username already exists)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    // ===========================================================
    // UPDATE USER
    // ===========================================================
    @Operation(
            summary = "Update a user - Only admin",
            description = "Allows an ADMIN to update an existing user's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserRequestDto dto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    // ===========================================================
    // UPDATE ROLE
    // ===========================================================
    @Operation(
            summary = "Update a user's role - Only admin",
            description = "Allows an ADMIN to update the role of an existing user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/role")
    public ResponseEntity<String> updateUserRole(@RequestBody @Valid UpdateUserRoleRequest request) {
        userService.updateUserRole(request.userId(), request.roleName());
        return ResponseEntity.ok("User role updated successfully");
    }

    // ===========================================================
    // GET USER BY ID
    // ===========================================================
    @Operation(
            summary = "Get a user by ID - Only admin",
            description = "Fetches a user by their unique ID. Accessible by ADMIN, SALES, or MANAGER.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ===========================================================
    // DELETE USER
    // ===========================================================
    @Operation(
            summary = "Delete a user - Only admin",
            description = "Allows an ADMIN to delete a user by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    // ===========================================================
    // SEARCH USERS
    // ===========================================================
    @Operation(
            summary = "Search users by keyword - Only admin",
            description = "Allows ADMIN, SALES, or MANAGER to search for users by username, email, or other fields.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUser(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUser(keyword));
    }

    // ===========================================================
    // GET ALL USERS
    // ===========================================================
    @Operation(
            summary = "Get all users - Only admin",
            description = "Fetches all users in the system. Accessible by ADMIN only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ===========================================================
    // UPDATE USER IMAGE
    // ===========================================================
    @Operation(summary = "Modifier l'image d'un utilisateur - Only admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping(value = "/{userId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> updateUserImage(
            @PathVariable Long userId,
            @Parameter(description = "Fichier image à uploader", required = true)
            @RequestPart("image") MultipartFile image
    ) throws Exception {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("L'image ne peut pas être vide");
        }

        UserResponseDto updatedUser = userService.updateUserImage(userId, image);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    @Operation(
            summary = "Update own password - user authenticated",
            description = "Allows an authenticated user to update their own password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password updated successfully",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or validation failed",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePasswordByUsername(userDetails.getUsername(), request.password());
        return ResponseEntity.ok("Password updated successfully");
    }


    @Operation(
            summary = "Get all roles",
            description = "Fetches all available roles in the system. Accessible by ADMIN only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Operation(
            summary = "Update account lock status - Only admin",
            description = "Allows an ADMIN to lock or unlock a user account for security purposes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account lock status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update-lock-status")
    public ResponseEntity<UserResponseDto> updateAccountLockStatus(@RequestBody @Valid UpdateAccountLockStatusRequest request) {
        userService.updateAccountLockStatus(request.userId(), request.lock());
        return ResponseEntity.ok(userService.getUserById(request.userId()));
    }

    // ===========================================================
    // UPDATE CREDENTIALS EXPIRY STATUS
    // ===========================================================
    @Operation(
            summary = "PRIVATE: Met à jour le statut d'expiration des identifiants - Only admin",
            description = "Permet de définir si les identifiants d’un utilisateur sont expirés ou non."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut des identifiants mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update-credentials-expiry-status")
    public ResponseEntity<String> updateCredentialsExpiryStatus(
            @org.springframework.web.bind.annotation.RequestBody UpdateCredentialsExpiryStatusRequest request
    ) {
        userService.updateCredentialsExpiryStatus(request.getUserId(), request.isExpire());
        return ResponseEntity.ok("Credentials expiry status updated");
    }

    // ===========================================================
    // UPDATE ACCOUNT ENABLED STATUS
    // ===========================================================
    @Operation(
            summary = "PRIVATE: Active ou désactive un compte utilisateur - Only admin",
            description = "Permet d’activer ou de désactiver un compte utilisateur en fonction de l’ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut du compte mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update-enabled-status")
    public ResponseEntity<String> updateAccountEnabledStatus(
            @org.springframework.web.bind.annotation.RequestBody UpdateAccountEnabledStatusRequest request
    ) {
        userService.updateAccountEnabledStatus(request.getUserId(), request.isEnabled());
        return ResponseEntity.ok("Account enabled status updated");
    }

    // ===========================================================
    // UPDATE ACCOUNT EXPIRY STATUS
    // ===========================================================
    @Operation(
            summary = "PRIVATE: Met à jour le statut d’expiration du compte - Only admin",
            description = "Permet de définir si le compte utilisateur est expiré ou non."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut d'expiration du compte mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update-expiry-status")
    public ResponseEntity<String> updateAccountExpiryStatus(
            @org.springframework.web.bind.annotation.RequestBody UpdateAccountExpiryStatusRequest request
    ) {
        userService.updateAccountExpiryStatus(request.getUserId(), request.isExpire());
        return ResponseEntity.ok("Account expiry status updated");
    }


}