package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.dto.UpdateUserRoleRequest;
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

    // ===========================================================
    // CREATE USER
    // ===========================================================
    @Operation(
            summary = "Create a new user",
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
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    // ===========================================================
    // UPDATE USER
    // ===========================================================
    @Operation(
            summary = "Update a user",
            description = "Allows an ADMIN to update an existing user's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    //@PreAuthorize("hasRole('ADMIN')")
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
            summary = "Update a user's role",
            description = "Allows an ADMIN to update the role of an existing user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/role")
    public ResponseEntity<String> updateUserRole(@RequestBody @Valid UpdateUserRoleRequest request) {
        userService.updateUserRole(request.userId(), request.roleName());
        return ResponseEntity.ok("User role updated successfully");
    }

    // ===========================================================
    // GET USER BY ID
    // ===========================================================
    @Operation(
            summary = "Get a user by ID",
            description = "Fetches a user by their unique ID. Accessible by ADMIN, SALES, or MANAGER.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    //@PreAuthorize("hasAnyRole('ADMIN','SALES','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ===========================================================
    // DELETE USER
    // ===========================================================
    @Operation(
            summary = "Delete a user",
            description = "Allows an ADMIN to delete a user by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    // ===========================================================
    // SEARCH USERS
    // ===========================================================
    @Operation(
            summary = "Search users by keyword",
            description = "Allows ADMIN, SALES, or MANAGER to search for users by username, email, or other fields.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    //@PreAuthorize("hasAnyRole('ADMIN','SALES','MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUser(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUser(keyword));
    }


    // ===========================================================
    // UPDATE USER IMAGE
    // ===========================================================
    @Operation(summary = "Modifier l’image d’un utilisateur")
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
            @RequestPart("image") MultipartFile image // ✅ utilisation de @RequestPart
    ) throws Exception {
        // log.info("Mise à jour de l'image pour l'utilisateur {}", userId);

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("L'image ne peut pas être vide");
        }

        UserResponseDto updatedUser = userService.updateUserImage(userId, image);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // ===========================================================
    // GET PRESIGNED USER IMAGE URL
    // ===========================================================
    @Operation(summary = "PUBLIC: Obtenir le lien temporaire de l’image d’un utilisateur")
    @GetMapping("/{id}/image-url")
    public ResponseEntity<String> getPresignedUserImageUrl(@PathVariable Long id) {
        String presignedUrl = userService.getPresignedImageUrl(id);
        return ResponseEntity.ok(presignedUrl);
    }

    // ===========================================================
    // GET ALL USERS
    // ===========================================================
    @Operation(
            summary = "Get all users",
            description = "Fetches all users in the system. Accessible by ADMIN, SALES, or MANAGER.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    //@PreAuthorize("hasAnyRole('ADMIN','SALES','MANAGER')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
