package com.musicshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Login Request DTO
 *
 * <p>Data transfer object for user login requests. Supports login with
 * username or email.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Username or email
     */
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    /**
     * Password
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Remember me (extend token expiration)
     */
    private Boolean rememberMe = false;

}
