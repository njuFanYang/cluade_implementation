package com.musicshare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response DTO
 *
 * <p>Data transfer object for login response containing JWT token and user information.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT access token
     */
    private String token;

    /**
     * Token type (always "Bearer")
     */
    private String tokenType = "Bearer";

    /**
     * Token expiration time in milliseconds
     */
    private Long expiresIn;

    /**
     * User information
     */
    private UserResponse userInfo;

    /**
     * Constructor with token and user info
     *
     * @param token JWT token
     * @param userInfo User information
     */
    public LoginResponse(String token, UserResponse userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    /**
     * Constructor with all fields
     *
     * @param token JWT token
     * @param expiresIn Token expiration time
     * @param userInfo User information
     */
    public LoginResponse(String token, Long expiresIn, UserResponse userInfo) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }

}
