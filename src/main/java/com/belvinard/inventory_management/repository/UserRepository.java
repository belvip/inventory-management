package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);

    List<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String keyword, String keyword1);

    //User existsByEmail(String email);

    //User existsByUserName(String userName);
}