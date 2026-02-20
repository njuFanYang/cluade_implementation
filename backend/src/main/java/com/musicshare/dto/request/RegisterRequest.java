package com.musicshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Registration Request DTO
 *
 * <p>Data transfer object for user registration requests. Contains validation
 * rules to ensure data integrity.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Username (3-20 characters, alphanumeric and underscores only)
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    /**
     * Email address
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Password (at least 8 characters, must contain letters and numbers)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain both letters and numbers")
    private String password;

    /**
     * Confirm password
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    /**
     * Nickname (optional)
     */
    @Size(max = 50, message = "Nickname cannot exceed 50 characters")
    private String nickname;

}
