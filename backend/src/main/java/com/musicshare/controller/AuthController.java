package com.musicshare.controller;

import com.musicshare.dto.request.LoginRequest;
import com.musicshare.dto.request.RegisterRequest;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.LoginResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 *
 * <p>Handles user authentication operations including registration and login.
 * These endpoints are publicly accessible and don't require authentication.
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>POST /api/auth/register - User registration</li>
 *   <li>POST /api/auth/login - User login</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;

    /**
     * User registration endpoint
     *
     * @param request Registration request containing user information
     * @return ApiResponse with registered user information
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user",
               description = "Create a new user account with username, email, and password")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration request for username: {}", request.getUsername());

        UserResponse userResponse = userService.register(request);

        log.info("User registered successfully: {}", userResponse.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", userResponse));
    }

    /**
     * User login endpoint
     *
     * @param request Login request containing username/email and password
     * @return ApiResponse with JWT token and user information
     */
    @PostMapping("/login")
    @Operation(summary = "User login",
               description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsernameOrEmail());

        LoginResponse loginResponse = userService.login(request);

        log.info("User logged in successfully: {}", request.getUsernameOrEmail());
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    /**
     * Logout endpoint (client-side token removal)
     *
     * @return Success message
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout",
               description = "Logout endpoint (token should be removed on client side)")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT is stateless, so logout is handled on client side by removing token
        // This endpoint is mainly for consistent API design
        log.info("Logout request received");
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

}
