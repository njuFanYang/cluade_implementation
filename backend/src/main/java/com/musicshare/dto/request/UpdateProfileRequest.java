package com.musicshare.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Profile Request DTO
 *
 * <p>Data transfer object for updating user profile information.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /**
     * Nickname
     */
    @Size(max = 50, message = "Nickname cannot exceed 50 characters")
    private String nickname;

    /**
     * Avatar URL
     */
    @Size(max = 500, message = "Avatar URL cannot exceed 500 characters")
    private String avatar;

    /**
     * Bio/description
     */
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    /**
     * Gender (M: Male, F: Female, O: Other, U: Unspecified)
     */
    private String gender;

    /**
     * Phone number
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    /**
     * Location
     */
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

}
