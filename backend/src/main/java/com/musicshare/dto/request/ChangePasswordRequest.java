package com.musicshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Change Password Request DTO
 *
 * <p>Data transfer object for changing user password.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    /**
     * Old password
     */
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain both letters and numbers")
    private String newPassword;

    /**
     * Confirm new password
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

}
