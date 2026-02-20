package com.musicshare.controller;

import com.musicshare.dto.request.ChangePasswordRequest;
import com.musicshare.dto.request.UpdateProfileRequest;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.service.UserService;
import com.musicshare.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 *
 * <p>Handles user profile and account management operations. All endpoints
 * require authentication except for public profile viewing.
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>GET /api/users/{id} - Get user by ID (public)</li>
 *   <li>GET /api/users/profile - Get current user profile</li>
 *   <li>PUT /api/users/profile - Update current user profile</li>
 *   <li>PUT /api/users/password - Change password</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    /**
     * Get user by ID (public endpoint)
     *
     * @param id User ID
     * @return User profile information
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
               description = "Retrieve user profile information by user ID (public access)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Get user by ID request: {}", id);

        UserResponse userResponse = userService.getUserById(id);

        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * Get current user profile
     *
     * @return Current user's profile information
     */
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile",
               description = "Retrieve the authenticated user's profile information",
               security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
        String username = SecurityUtil.getCurrentUsername();
        log.info("Get profile request for user: {}", username);

        UserResponse userResponse = userService.getUserProfile(username);

        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * Update current user profile
     *
     * @param request Profile update request
     * @return Updated user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile",
               description = "Update the authenticated user's profile information",
               security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        String username = SecurityUtil.getCurrentUsername();
        log.info("Update profile request for user: {}", username);

        UserResponse userResponse = userService.updateProfile(username, request);

        log.info("Profile updated successfully for user: {}", username);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userResponse));
    }

    /**
     * Change password
     *
     * @param request Password change request
     * @return Success message
     */
    @PutMapping("/password")
    @Operation(summary = "Change password",
               description = "Change the authenticated user's password",
               security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        String username = SecurityUtil.getCurrentUsername();
        log.info("Change password request for user: {}", username);

        userService.changePassword(username, request);

        log.info("Password changed successfully for user: {}", username);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

}
