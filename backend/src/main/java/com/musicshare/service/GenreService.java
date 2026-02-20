package com.musicshare.service;

import com.musicshare.dto.response.GenreResponse;

import java.util.List;

/**
 * Genre Service Interface
 *
 * <p>Defines business logic operations for genre management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public interface GenreService {

    /**
     * Get all genres
     *
     * @return List of all genres
     */
    List<GenreResponse> getAllGenres();

    /**
     * Get popular genres ordered by music count
     *
     * @return List of popular genres
     */
    List<GenreResponse> getPopularGenres();

    /**
     * Get genre by ID
     *
     * @param id Genre ID
     * @return GenreResponse
     * @throws com.musicshare.exception.BusinessException if genre not found
     */
    GenreResponse getGenreById(Long id);
}
