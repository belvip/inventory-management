package com.belvinard.inventory_management.security;

import com.belvinard.inventory_management.model.AppRole;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) ->
                requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/demo/**").permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(withDefaults());
        return http.build();
    }


    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));


            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));
            Role managerRole = roleRepository.findByRoleName(AppRole.ROLE_MANAGER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_MANAGER)));

            Role salesRole = roleRepository.findByRoleName(AppRole.ROLE_SALES)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_SALES)));


            if (!userRepository.existsByUserName("user")) {
                User user1 = new User("user", "user@user.com", "{noop}password");
                user1.setFirstName("User");
                user1.setLastName("Test");
                user1.setAccountNonLocked(false);
                user1.setAccountNonExpired(true);
                user1.setCredentialsNonExpired(true);
                user1.setEnabled(true);
                user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
                user1.setTwoFactorEnabled(false);
                user1.setSignUpMethod("email");
                user1.setRole(userRole);
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@admin.com", "{noop}password");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnabled(true);
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
                admin.setTwoFactorEnabled(false);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

            if(!userRepository.existsByUserName("manager")) {
                User manager = new User("manager", "manager@manager.com", "{noop}password");
                manager.setFirstName("Manager");
                manager.setLastName("User");
                manager.setAccountNonLocked(true);
                manager.setAccountNonExpired(true);
                manager.setCredentialsNonExpired(true);
                manager.setEnabled(true);
                manager.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                manager.setAccountExpiryDate(LocalDate.now().plusYears(1));
                manager.setTwoFactorEnabled(false);
                manager.setSignUpMethod("email");
                manager.setRole(managerRole);
                userRepository.save(manager);
            }

            if (!userRepository.existsByUserName("sales")) {
                User sales = new User("sales", "sales@sales.com", "{noop}password");
                sales.setFirstName("Sales");
                sales.setLastName("User");
                sales.setAccountNonLocked(true);
                sales.setAccountNonExpired(true);
                sales.setCredentialsNonExpired(true);
                sales.setEnabled(true);
                sales.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                sales.setAccountExpiryDate(LocalDate.now().plusYears(1));
                sales.setTwoFactorEnabled(false);
                sales.setSignUpMethod("email");
                sales.setRole(salesRole);
                userRepository.save(sales);
            }
        };
    }






}
