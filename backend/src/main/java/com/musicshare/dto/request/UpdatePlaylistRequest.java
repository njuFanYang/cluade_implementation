package com.musicshare.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Playlist Request DTO
 *
 * <p>Data transfer object for playlist update requests.
 * All fields are optional for partial updates.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlaylistRequest {

    /**
     * Playlist name
     */
    @Size(max = 100, message = "Playlist name cannot exceed 100 characters")
    private String name;

    /**
     * Playlist description
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Cover image URL
     */
    @Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    private String coverImage;

    /**
     * Public visibility flag
     */
    private Boolean isPublic;
}
