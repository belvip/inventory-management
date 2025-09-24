package com.belvinard.inventory_management.security;

import com.belvinard.inventory_management.config.OAuth2LoginSuccessHandler;
import com.belvinard.inventory_management.model.AppRole;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.repository.UserRepository;
import com.belvinard.inventory_management.security.jwt.AuthEntryPointJwt;
import com.belvinard.inventory_management.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDate;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_SALES = "SALES";
    private static final String DEFAULT_PASSWORD = System.getenv().getOrDefault("DEFAULT_USER_PASSWORD", "ChangeMe123!");
    private static final String EMAIL_SIGNUP_METHOD = "email";

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    @Lazy
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(requests ->
                requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/demo/**").permitAll()
                        .requestMatchers("/api/v1/auth/oauth2/success").permitAll()
                        .requestMatchers("/api/v1/users/update-password").authenticated()
                        .requestMatchers("/api/v1/users/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/companies/all").permitAll()
                        .requestMatchers("/api/v1/categories/all").permitAll()
                        .requestMatchers("/api/v1/categories/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/categories/update").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/categories/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1//by-company/{companyId}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/companies/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/companies/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/companies/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/articles/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/articles/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/articles/update/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/articles/code/{code}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/articles/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/articles/{id}/image").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/clients/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/orders/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/order-lines /**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/sales /**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()
                        .anyRequest().authenticated())
                        .oauth2Login(oauth ->
                            oauth.successHandler(oAuth2LoginSuccessHandler)
                        );
        http.exceptionHandling(exception
                -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
            session.maximumSessions(1);
        });
        http.addFilterBefore(authTokenFilter,
                UsernamePasswordAuthenticationFilter.class);

        // Disable form login and HTTP basic for JWT-only authentication
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));


            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));
            Role managerRole = roleRepository.findByRoleName(AppRole.ROLE_MANAGER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_MANAGER)));

            Role salesRole = roleRepository.findByRoleName(AppRole.ROLE_SALES)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_SALES)));


            createUserIfNotExists("user", "user@user.com", "User", "Test", userRole, userRepository, passwordEncoder);
            createUserIfNotExists("admin", "admin@admin.com", "Admin", "User", adminRole, userRepository, passwordEncoder);
            createUserIfNotExists("manager", "manager@manager.com", "Manager", "User", managerRole, userRepository, passwordEncoder);
            createUserIfNotExists("sales", "sales@sales.com", "Sales", "User", salesRole, userRepository, passwordEncoder);
        };
    }

    private void createUserIfNotExists(String username, String email, String firstName, String lastName, 
                                     Role role, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        if (!userRepository.existsByUserName(username)) {
            User user = new User(username, email, passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusDays(90));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod(EMAIL_SIGNUP_METHOD);
            user.setRole(role);
            userRepository.save(user);
        }
    }
}
