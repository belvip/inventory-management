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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDate;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_SALES = "SALES";
    private static final String ROLE_USER = "USER";
    private static final String EMAIL_SIGNUP_METHOD = "email";

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    @Lazy
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final Environment env;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource));
        http.authorizeHttpRequests(requests ->
                requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/demo/**").permitAll()
                        .requestMatchers("/api/cors-test/**").permitAll()
                        .requestMatchers("/api/v1/auth/oauth2/success").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/api/v1/users/update-password").authenticated()
                        .requestMatchers("/api/v1/users/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/companies/all").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/categories/all").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/categories/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/categories/update").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/categories/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/categories/by-company/{companyId}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/companies/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/companies/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/companies/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/articles/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/articles/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/articles/code/{code}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/articles/all").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/articles/archived").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/articles/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/clients/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/orders/{id}/cancel").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/orders/all").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/orders/client/{clientId}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/orders/status/{status}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES, ROLE_USER)
                        .requestMatchers("/api/v1/orders/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/order-lines/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/sales/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/suppliers/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-orders/create").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/supplier-orders/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-orders/{id}/status").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/supplier-orders/{id}/cancel").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/supplier-orders/code/{code}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-orders/all").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-orders/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/v1/supplier-order-lines/add").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/supplier-order-lines/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/supplier-order-lines/{id}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                        .requestMatchers("/api/v1/supplier-order-lines/order/{supplierOrderId}").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-order-lines/order/{supplierOrderId}/total").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-order-lines/all").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_SALES)
                        .requestMatchers("/api/v1/supplier-order-lines/**").hasRole(ROLE_ADMIN)
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


            if (!userRepository.existsByUserName("user")) {
                User user1 = new User("user", "user@user.com",
                        passwordEncoder.encode("password"));
                user1.setFirstName("User");
                user1.setLastName("Test");
                user1.setAccountNonLocked(true);
                user1.setAccountNonExpired(true);
                user1.setCredentialsNonExpired(true);
                user1.setEnabled(true);
                user1.setCredentialsExpiryDate(LocalDate.now().plusDays(90));
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
                user1.setTwoFactorEnabled(false);
                user1.setSignUpMethod("email");
                user1.setRole(userRole);
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@admin.com",
                        passwordEncoder.encode("password"));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnabled(true);
                admin.setCredentialsExpiryDate(LocalDate.now().plusDays(90));
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
                admin.setTwoFactorEnabled(false);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

            if(!userRepository.existsByUserName("manager")) {
                User manager = new User("manager", "manager@manager.com",
                        passwordEncoder.encode("password"));
                manager.setFirstName("Manager");
                manager.setLastName("User");
                manager.setAccountNonLocked(true);
                manager.setAccountNonExpired(true);
                manager.setCredentialsNonExpired(true);
                manager.setEnabled(true);
                manager.setCredentialsExpiryDate(LocalDate.now().plusDays(90));
                manager.setAccountExpiryDate(LocalDate.now().plusYears(1));
                manager.setTwoFactorEnabled(false);
                manager.setSignUpMethod("email");
                manager.setRole(managerRole);
                userRepository.save(manager);
            }

            if (!userRepository.existsByUserName("sales")) {
                User sales = new User("sales", "sales@sales.com",
                        passwordEncoder.encode("password"));
                sales.setFirstName("Sales");
                sales.setLastName("User");
                sales.setAccountNonLocked(true);
                sales.setAccountNonExpired(true);
                sales.setCredentialsNonExpired(true);
                sales.setEnabled(true);
                sales.setCredentialsExpiryDate(LocalDate.now().plusDays(90));
                sales.setAccountExpiryDate(LocalDate.now().plusYears(1));
                sales.setTwoFactorEnabled(false);
                sales.setSignUpMethod("email");
                sales.setRole(salesRole);
                userRepository.save(sales);
            }
        };
    }

}