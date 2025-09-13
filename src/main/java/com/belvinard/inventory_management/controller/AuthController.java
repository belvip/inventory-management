package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.UserResponseDto;
import com.belvinard.inventory_management.model.AppRole;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.repository.UserRepository;
import com.belvinard.inventory_management.security.jwt.JwtUtils;
import com.belvinard.inventory_management.security.request.LoginRequest;
import com.belvinard.inventory_management.security.request.SignupRequest;
import com.belvinard.inventory_management.security.response.LoginResponse;
import com.belvinard.inventory_management.security.response.MessageResponse;
import com.belvinard.inventory_management.security.response.UserInfoResponse;
import com.belvinard.inventory_management.service.UserService;
import com.belvinard.inventory_management.service.RefreshTokenService;
import com.belvinard.inventory_management.model.RefreshToken;
import com.belvinard.inventory_management.security.request.TokenRefreshRequest;
import com.belvinard.inventory_management.security.response.TokenRefreshResponse;
import com.belvinard.inventory_management.exception.TokenRefreshException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for user login")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "User login",
            description = "Authenticate user with username/password and return JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Bad credentials", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid request format", content = @Content)
            }
    )
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        // set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Créer le refresh token
        User user = userRepository.findByUserName(userDetails.getUsername()).get();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Prepare the response body with JWT and refresh token
        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken, refreshToken.getToken());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "User registration",
            description = "Register a new user account with default ROLE_USER. Username and email must be unique.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Username or email already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Set user details from SignupRequest
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setImage(signUpRequest.getImage());
        user.setSignUpMethod(signUpRequest.getSignUpMethod());

        // Set address if provided
        if (signUpRequest.getAddress() != null) {
            user.setAddress(new com.belvinard.inventory_management.model.Address(
                signUpRequest.getAddress().address1(),
                signUpRequest.getAddress().address2(),
                signUpRequest.getAddress().city(),
                signUpRequest.getAddress().postalCode(),
                signUpRequest.getAddress().country()
            ));
        }

        // Set default role as USER for public signup
        Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRole(role);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusDays(90)); // Mot de passe expire en 90 jours
        user.setAccountExpiryDate(LocalDate.now().plusYears(1)); // Compte expire en 1 an
        user.setTwoFactorEnabled(false);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @Operation(
            summary = "Get current user details",
            description = "Get detailed information about the currently authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto userDto = userService.findByUsername(userDetails.getUsername());
        
        // Récupérer l'entité User pour les champs sensibles (uniquement pour le profil utilisateur)
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                userDto.userId(),
                userDto.firstName(),
                userDto.lastName(),
                userDto.userName(),
                userDto.email(),
                userDto.image(),
                userDto.address(),
                userDto.accountNonLocked(),
                userDto.accountNonExpired(),
                userDto.credentialsNonExpired(),
                userDto.enabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                roles
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get current username",
            description = "Get the username of the currently authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Username retrieved successfully",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    @Operation(
            summary = "Refresh JWT token",
            description = "Generate new JWT token using refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenRefreshResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Refresh token expired", content = @Content)
            }
    )
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUserName());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @Operation(
            summary = "User logout",
            description = "Logout user and invalidate refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
            }
    )
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUserName(userDetails.getUsername()).get();
        refreshTokenService.deleteByUserId(user.getUserId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}

