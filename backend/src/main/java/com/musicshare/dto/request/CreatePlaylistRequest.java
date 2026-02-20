package com.musicshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Playlist Request DTO
 *
 * <p>Data transfer object for playlist creation requests.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaylistRequest {

    /**
     * Playlist name
     */
    @NotBlank(message = "Playlist name is required")
    @Size(max = 100, message = "Playlist name cannot exceed 100 characters")
    private String name;

    /**
     * Playlist description (optional)
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Cover image URL (optional)
     */
    @Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    private String coverImage;

    /**
     * Public visibility flag
     */
    private Boolean isPublic = true;
}
