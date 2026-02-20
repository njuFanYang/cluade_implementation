package com.musicshare.dto.response;

import com.musicshare.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Genre Response DTO
 *
 * <p>Data transfer object for genre information in API responses.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse {

    private Long id;
    private String name;
    private String nameZh;
    private String description;
    private String icon;
    private Integer musicCount;
    private LocalDateTime createdAt;

    /**
     * Create GenreResponse from Genre entity
     *
     * @param genre Genre entity
     * @return GenreResponse instance
     */
    public static GenreResponse fromEntity(Genre genre) {
        if (genre == null) {
            return null;
        }
        GenreResponse response = new GenreResponse();
        response.setId(genre.getId());
        response.setName(genre.getName());
        response.setNameZh(genre.getNameZh());
        response.setDescription(genre.getDescription());
        response.setIcon(genre.getIcon());
        response.setMusicCount(genre.getMusicCount());
        response.setCreatedAt(genre.getCreatedAt());
        return response;
    }
}
