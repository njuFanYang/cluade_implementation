package com.musicshare.controller;

import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.GenreResponse;
import com.musicshare.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Genre Controller
 *
 * <p>Handles genre-related operations.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Tag(name = "Genre", description = "Music genre APIs")
public class GenreController {

    private final GenreService genreService;

    /**
     * Get all genres
     *
     * @return List of all genres
     */
    @GetMapping
    @Operation(summary = "Get all genres",
            description = "Retrieve all music genres")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAllGenres() {
        log.debug("Get all genres request");

        List<GenreResponse> response = genreService.getAllGenres();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get popular genres
     *
     * @return List of popular genres
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular genres",
            description = "Get genres ordered by music count")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getPopularGenres() {
        log.debug("Get popular genres request");

        List<GenreResponse> response = genreService.getPopularGenres();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get genre by ID
     *
     * @param id Genre ID
     * @return Genre details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID",
            description = "Retrieve genre information by ID")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(@PathVariable Long id) {
        log.debug("Get genre by ID: {}", id);

        GenreResponse response = genreService.getGenreById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
