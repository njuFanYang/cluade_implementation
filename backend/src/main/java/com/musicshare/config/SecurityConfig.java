package com.musicshare.config;

import com.musicshare.security.CustomUserDetailsService;
import com.musicshare.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

/**
 * Spring Security Configuration
 *
 * <p>Configures security settings including authentication, authorization,
 * JWT filter chain, and CORS/CSRF settings.
 *
 * <h3>Security Features:</h3>
 * <ul>
 *   <li>JWT-based stateless authentication</li>
 *   <li>BCrypt password encoding</li>
 *   <li>Role-based access control (RBAC)</li>
 *   <li>CORS enabled for frontend access</li>
 *   <li>CSRF disabled for REST API</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure HTTP security filter chain
     *
     * @param http HttpSecurity object
     * @return SecurityFilterChain
     * @throws Exception If configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/health/**").permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Public read access
                        .requestMatchers(HttpMethod.GET, "/api/music/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/playlists/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()

                        // Musician-only endpoints
                        .requestMatchers("/api/music/upload").hasAnyAuthority("ROLE_MUSICIAN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/music/**").hasAnyAuthority("ROLE_MUSICIAN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/music/**").hasAnyAuthority("ROLE_MUSICIAN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/music/**").hasAnyAuthority("ROLE_MUSICIAN", "ROLE_ADMIN")

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Set session management to stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before username/password authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure authentication provider
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configure authentication manager
     *
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception If configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure password encoder (BCrypt)
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
