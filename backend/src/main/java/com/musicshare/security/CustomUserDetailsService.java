package com.musicshare.security;

import com.musicshare.entity.Role;
import com.musicshare.entity.User;
import com.musicshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService Implementation
 *
 * <p>Loads user-specific data for Spring Security authentication. Retrieves
 * user information from database and converts to UserDetails object.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username for authentication
     *
     * @param usernameOrEmail Username or email
     * @return UserDetails object
     * @throws UsernameNotFoundException If user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(mapRolesToAuthorities(user.getRoles()))
                .accountLocked(user.getLocked())
                .disabled(!user.getEnabled())
                .build();
    }

    /**
     * Convert roles to granted authorities
     *
     * @param roles Set of roles
     * @return Collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

}
