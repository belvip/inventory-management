package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.AddressDto;
import com.belvinard.inventory_management.dto.ResetPasswordRequest;
import com.belvinard.inventory_management.dto.UserRequestDto;
import com.belvinard.inventory_management.dto.UserResponseDto;

import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.AddressMapper;
import com.belvinard.inventory_management.mapper.UserMapper;
import com.belvinard.inventory_management.model.*;
import com.belvinard.inventory_management.repository.PasswordResetTokenRepository;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.repository.UserRepository;
import com.belvinard.inventory_management.service.MinioService;
import com.belvinard.inventory_management.service.UserService;
import com.belvinard.inventory_management.utils.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Value("${frontend.url}")
    String frontendUrl;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AddressMapper addressMapper;
    private final MinioService minioService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

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
        
        // Définir les champs de sécurité
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(java.time.LocalDate.now().plusDays(90));
        user.setAccountExpiryDate(java.time.LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("admin_created");

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

    @Override
    public UserResponseDto updateUserImage(Long userId, MultipartFile image) throws IOException {
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String fileName = minioService.uploadImage(image);
        userFromDb.setImage(fileName);
        String imageUrl = minioService.getPreSignedUrl(fileName, 15);
        User updatedUser = userRepository.save(userFromDb);

        // mapper to dto
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return createResponseDto(user);
    }


    @Override
    public String getPresignedImageUrl(Long id) {
        // 1. Chercher l’article
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found !!"));

        // 2. Vérifier s’il y a une image
        String fileName = user.getImage();

        if (fileName == null || fileName.isBlank()) {
            throw new APIException("No image found for this article");
        }

        // 3. Retourner l’URL signée
        return minioService.getPreSignedUrl(fileName, 900); // 15 minutes
    }



    @Override
    @Transactional
    public void updatePasswordByUsername(String username, String password) {
        // Validation des paramètres
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Rechercher l'utilisateur
        User user = userRepository.findByUserName(username.trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        // Vérifier que le compte est actif
        if (!user.isEnabled()) {
            throw new IllegalStateException("Cannot update password for disabled user");
        }
        if (!user.isAccountNonLocked()) {
            throw new IllegalStateException("Cannot update password for locked user");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(password.trim()));
        
        // Mettre à jour la date d'expiration des credentials
        user.setCredentialsExpiryDate(java.time.LocalDate.now().plusDays(90));
        
        userRepository.save(user);
    }

    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);

        passwordResetTokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        // Send email to user
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);;


    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isUsed()){
            throw new RuntimeException("Password reset token has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    @Override
    public User registerUser(User user) {
        if(user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return  userRepository.save(user);
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