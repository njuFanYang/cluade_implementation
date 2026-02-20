package com.musicshare.service;

import com.musicshare.dto.request.ChangePasswordRequest;
import com.musicshare.dto.request.LoginRequest;
import com.musicshare.dto.request.RegisterRequest;
import com.musicshare.dto.request.UpdateProfileRequest;
import com.musicshare.dto.response.LoginResponse;
import com.musicshare.dto.response.UserResponse;

/**
 * User Service Interface
 *
 * <p>Defines business logic operations for user management, including
 * registration, authentication, profile management, and password operations.
 *
 * <h3>Main Features:</h3>
 * <ul>
 *   <li>User registration with validation</li>
 *   <li>User authentication with JWT token generation</li>
 *   <li>Profile management and updates</li>
 *   <li>Password change operations</li>
 *   <li>User retrieval by ID or username</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public interface UserService {

    /**
     * Register a new user
     *
     * <p>Creates a new user account with the provided registration information.
     * Validates that username and email are unique, and that passwords match.
     * Assigns default ROLE_USER role to new users.
     *
     * @param request Registration request containing username, email, password, etc.
     * @return UserResponse containing the created user's information
     * @throws com.musicshare.exception.BusinessException if username/email already exists or passwords don't match
     */
    UserResponse register(RegisterRequest request);

    /**
     * Authenticate user and generate JWT token
     *
     * <p>Validates user credentials and generates a JWT token for authenticated
     * access. Updates last login time and IP address.
     *
     * @param request Login request containing username/email and password
     * @return LoginResponse containing JWT token and user information
     * @throws com.musicshare.exception.BusinessException if credentials are invalid or account is disabled
     */
    LoginResponse login(LoginRequest request);

    /**
     * Get user profile by username
     *
     * <p>Retrieves detailed user information by username. This is a public
     * operation that can be used to view user profiles.
     *
     * @param username Username to search for
     * @return UserResponse containing user profile information
     * @throws com.musicshare.exception.BusinessException if user is not found
     */
    UserResponse getUserProfile(String username);

    /**
     * Update user profile
     *
     * <p>Updates user profile information such as nickname, avatar, bio,
     * gender, phone, and location. Only the authenticated user can update
     * their own profile.
     *
     * @param username Username of the user to update
     * @param request Update request containing profile fields to update
     * @return UserResponse containing updated user information
     * @throws com.musicshare.exception.BusinessException if user is not found or insufficient permissions
     */
    UserResponse updateProfile(String username, UpdateProfileRequest request);

    /**
     * Change user password
     *
     * <p>Changes the user's password after validating the old password.
     * Verifies that new password and confirmation match, and that the new
     * password is different from the old one.
     *
     * @param username Username of the user changing password
     * @param request Change password request containing old and new passwords
     * @throws com.musicshare.exception.BusinessException if old password is incorrect, passwords don't match, or user not found
     */
    void changePassword(String username, ChangePasswordRequest request);

    /**
     * Get user by ID
     *
     * <p>Retrieves user information by user ID. This is typically used for
     * internal operations or when the ID is known.
     *
     * @param id User ID to search for
     * @return UserResponse containing user information
     * @throws com.musicshare.exception.BusinessException if user is not found
     */
    UserResponse getUserById(Long id);

}
