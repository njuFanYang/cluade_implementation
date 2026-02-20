package com.musicshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Upload Music Request DTO
 *
 * <p>Data transfer object for music upload requests.
 * Contains metadata for the music file being uploaded.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadMusicRequest {

    /**
     * Music title
     */
    @NotBlank(message = "Music title is required")
    @Size(max = 200, message = "Music title cannot exceed 200 characters")
    private String title;

    /**
     * Artist name
     */
    @NotBlank(message = "Artist name is required")
    @Size(max = 100, message = "Artist name cannot exceed 100 characters")
    private String artist;

    /**
     * Album ID (optional)
     */
    private Long albumId;

    /**
     * Genre ID
     */
    @NotNull(message = "Genre is required")
    private Long genreId;

    /**
     * Music description (optional)
     */
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    /**
     * Lyrics (optional)
     */
    @Size(max = 10000, message = "Lyrics cannot exceed 10000 characters")
    private String lyrics;

    /**
     * Release date (optional)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    /**
     * Public visibility flag
     */
    private Boolean isPublic = true;
}
