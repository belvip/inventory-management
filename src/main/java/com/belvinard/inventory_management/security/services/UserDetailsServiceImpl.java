
package com.belvinard.inventory_management.security.services;

import com.belvinard.inventory_management.model.User;
import com.belvinard.inventory_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find by email first (for OAuth2), then by username
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUserName(username))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username/email: " + username
                ));

        return UserDetailsImpl.build(user);
    }
}
