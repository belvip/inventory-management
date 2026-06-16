package com.belvinard.inventory_management.config;

import com.belvinard.inventory_management.model.AppRole;
import com.belvinard.inventory_management.model.Role;
import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.RoleRepository;
import com.belvinard.inventory_management.security.jwt.JwtUtils;
import com.belvinard.inventory_management.security.services.UserDetailsImpl;
import com.belvinard.inventory_management.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ApplicationContext applicationContext;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;


    @Value("${frontend.url}")
    private String frontendUrl;


    String username;
    String idAttributeKey;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) || "google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();
            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                String githubLogin = attributes.getOrDefault("login", "").toString();
                username = adjustUsernameLength(githubLogin);
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                String emailPrefix = email.split("@")[0];
                username = adjustUsernameLength(emailPrefix);
                idAttributeKey = "sub";
            } else {
                username = "user";
                idAttributeKey = "id";
            }
            System.out.println("HELLO : " + email + " : " + name + " : " + username);


            getUserService().findByEmail(email)
                    .ifPresentOrElse(user -> {
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        User newUser = new User();
                        Optional<Role> userRole = roleRepository.findByRoleName(AppRole.ROLE_USER); // Fetch existing role
                        if (userRole.isPresent()) {
                            newUser.setRole(userRole.get()); // Set existing role
                        } else {
                            // Handle the case where the role is not found
                            throw new RuntimeException("Default role not found");
                        }
                        newUser.setEmail(email);
                        newUser.setUserName(username);
                        newUser.setFirstName(name.split(" ")[0].isEmpty() ? "User" : name.split(" ")[0]);
                        newUser.setLastName(name.split(" ").length > 1 ? name.split(" ")[1] : "OAuth");
                        newUser.setSignUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        getUserService().registerUser(newUser);
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });
        }
        this.setAlwaysUseDefaultTargetUrl(true);


        // JWT TOKEN LOGIC
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();


        // Extract necessary attributes
        String email = (String) attributes.get("email");
        System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);


        // Get user from database to get role
        User currentUser = getUserService().findByEmail(email).orElse(null);
        String userRole = currentUser != null ? currentUser.getRole().getRoleName().name() : "ROLE_USER";
        
        // Generate JWT token with claims - use email as username for consistency
        String jwtToken = jwtUtils.generateTokenWithClaims(email, email, userRole);


        // Redirect to frontend with JWT token
        // Use only the first URL if multiple URLs are configured
        String redirectUrl = frontendUrl.contains(",") ? frontendUrl.split(",")[0].trim() : frontendUrl;
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl + "/auth/callback")
                .queryParam("token", jwtToken)
                .build().toUriString();
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private UserService getUserService() {
        return applicationContext.getBean(UserService.class);
    }
    
    private String adjustUsernameLength(String originalUsername) {
        if (originalUsername == null || originalUsername.isEmpty()) {
            return "user";
        }
        
        // Si trop court, ajouter des chiffres
        if (originalUsername.length() < 4) {
            return originalUsername + "123".substring(0, 4 - originalUsername.length());
        }
        
        // Si trop long, tronquer à 10 caractères
        if (originalUsername.length() > 10) {
            return originalUsername.substring(0, 10);
        }
        
        return originalUsername;
    }
}



