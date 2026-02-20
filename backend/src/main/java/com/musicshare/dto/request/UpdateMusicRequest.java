package com.musicshare.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Update Music Request DTO
 *
 * <p>Data transfer object for music update requests.
 * All fields are optional for partial updates.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMusicRequest {

    /**
     * Music title
     */
    @Size(max = 200, message = "Music title cannot exceed 200 characters")
    private String title;

    /**
     * Artist name
     */
    @Size(max = 100, message = "Artist name cannot exceed 100 characters")
    private String artist;

    /**
     * Album ID
     */
    private Long albumId;

    /**
     * Genre ID
     */
    private Long genreId;

    /**
     * Music description
     */
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    /**
     * Lyrics
     */
    @Size(max = 10000, message = "Lyrics cannot exceed 10000 characters")
    private String lyrics;

    /**
     * Release date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    /**
     * Public visibility flag
     */
    private Boolean isPublic;

    /**
     * Cover image URL
     */
    @Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    private String coverImage;
}
