package com.musicshare.service.impl;

import com.musicshare.dto.request.ChangePasswordRequest;
import com.musicshare.dto.request.LoginRequest;
import com.musicshare.dto.request.RegisterRequest;
import com.musicshare.dto.request.UpdateProfileRequest;
import com.musicshare.dto.response.LoginResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.entity.Role;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.RoleRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * User Service Implementation
 *
 * <p>Implements business logic for user management operations including
 * registration, authentication, profile management, and password operations.
 * Uses Spring Security for authentication and BCrypt for password encoding.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>User registration with validation and role assignment</li>
 *   <li>JWT-based authentication</li>
 *   <li>Profile updates with validation</li>
 *   <li>Secure password management</li>
 *   <li>Transaction management for data consistency</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     *
     * <p>Creates a new user account with the following steps:
     * <ol>
     *   <li>Validate that passwords match</li>
     *   <li>Check if username already exists</li>
     *   <li>Check if email already exists</li>
     *   <li>Encode password using BCrypt</li>
     *   <li>Assign default ROLE_USER role</li>
     *   <li>Save user to database</li>
     * </ol>
     *
     * @param request Registration request containing user information
     * @return UserResponse containing the created user's information
     * @throws BusinessException if validation fails or user already exists
     */
    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.getUsername());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Registration failed - password mismatch for username: {}", request.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEnabled(true);
        user.setLocked(false);

        // Assign default ROLE_USER role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("Default role ROLE_USER not found in database");
                    return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Default role not found");
                });
        user.addRole(userRole);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("Successfully registered new user: {}", savedUser.getUsername());

        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Authenticate user and generate JWT token
     *
     * <p>Authenticates user credentials and performs the following:
     * <ol>
     *   <li>Determine if login is by username or email</li>
     *   <li>Authenticate credentials using Spring Security</li>
     *   <li>Generate JWT token with appropriate expiration</li>
     *   <li>Update last login time (optional IP tracking)</li>
     *   <li>Return token and user information</li>
     * </ol>
     *
     * @param request Login request containing username/email and password
     * @return LoginResponse containing JWT token and user information
     * @throws BusinessException if credentials are invalid or account is disabled
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for: {}", request.getUsernameOrEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Determine username from authentication
            String username = authentication.getName();

            // Find user by username
            User user = userRepository.findByUsernameWithRoles(username)
                    .orElseThrow(() -> {
                        log.error("User not found after successful authentication: {}", username);
                        return new BusinessException(ErrorCode.USER_NOT_FOUND);
                    });

            // Check if account is disabled
            if (!user.getEnabled()) {
                log.warn("Login attempt for disabled account: {}", username);
                throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
            }

            // Generate JWT token with custom expiration if "remember me" is enabled
            Long expirationMs = jwtTokenProvider.getExpirationTime();
            if (Boolean.TRUE.equals(request.getRememberMe())) {
                // Extend token expiration to 30 days for "remember me"
                expirationMs = 30L * 24 * 60 * 60 * 1000; // 30 days in milliseconds
            }

            String token = jwtTokenProvider.generateToken(authentication);

            // Update last login time
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);

            log.info("User logged in successfully: {}", username);

            // Create login response
            UserResponse userResponse = UserResponse.fromEntity(user);
            return new LoginResponse(token, expirationMs, userResponse);

        } catch (DisabledException e) {
            log.warn("Login attempt for disabled account: {}", request.getUsernameOrEmail());
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for: {}", request.getUsernameOrEmail());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed for {}: {}", request.getUsernameOrEmail(), e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Login failed");
        }
    }

    /**
     * Get user profile by username
     *
     * <p>Retrieves user profile information for display purposes.
     * Does not include sensitive information like password.
     *
     * @param username Username to search for
     * @return UserResponse containing user profile information
     * @throws BusinessException if user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String username) {
        log.info("Fetching user profile: {}", username);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        return UserResponse.fromEntity(user);
    }

    /**
     * Update user profile
     *
     * <p>Updates user profile information. Only non-null fields in the request
     * are updated, allowing for partial updates.
     *
     * @param username Username of the user to update
     * @param request Update request containing profile fields to update
     * @return UserResponse containing updated user information
     * @throws BusinessException if user is not found
     */
    @Override
    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found for profile update: {}", username);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        // Update only non-null fields
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", username);

        return UserResponse.fromEntity(updatedUser);
    }

    /**
     * Change user password
     *
     * <p>Changes user password with the following validations:
     * <ol>
     *   <li>Verify old password is correct</li>
     *   <li>Verify new password and confirmation match</li>
     *   <li>Verify new password is different from old password</li>
     *   <li>Encode and save new password</li>
     * </ol>
     *
     * @param username Username of the user changing password
     * @param request Change password request containing old and new passwords
     * @throws BusinessException if validation fails
     */
    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        log.info("Attempting password change for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found for password change: {}", username);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        // Verify old password is correct
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Old password incorrect for user: {}", username);
            throw new BusinessException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        // Verify new password and confirmation match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("New password and confirmation do not match for user: {}", username);
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Verify new password is different from old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("New password is the same as old password for user: {}", username);
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "New password must be different from old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

    /**
     * Get user by ID
     *
     * <p>Retrieves user information by user ID. Useful for internal
     * operations and when the user ID is known.
     *
     * @param id User ID to search for
     * @return UserResponse containing user information
     * @throws BusinessException if user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        return UserResponse.fromEntity(user);
    }

}
