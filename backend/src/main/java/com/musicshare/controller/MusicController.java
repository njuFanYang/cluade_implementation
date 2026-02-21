package com.musicshare.controller;

import com.musicshare.dto.request.UpdateMusicRequest;
import com.musicshare.dto.request.UploadMusicRequest;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.MusicDetailResponse;
import com.musicshare.dto.response.MusicResponse;
import com.musicshare.service.MusicService;
import com.musicshare.service.UserService;
import com.musicshare.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Music Controller
 *
 * <p>Handles music-related operations including upload, retrieval, update,
 * delete, search, and streaming.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
@Tag(name = "Music", description = "Music management APIs")
public class MusicController {

    private final MusicService musicService;
    private final UserService userService;

    /**
     * Upload music file with metadata
     *
     * @param request Music metadata
     * @param file    Music file
     * @return Uploaded music information
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload music",
            description = "Upload a new music file with metadata (requires MUSICIAN or ADMIN role)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("hasAnyRole('ROLE_MUSICIAN', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MusicDetailResponse>> uploadMusic(
            @Valid @ModelAttribute UploadMusicRequest request,
            @RequestParam("file") MultipartFile file) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Upload music request from user: {} (ID: {})", username, userId);

        MusicDetailResponse response = musicService.uploadMusic(request, file, userId);

        log.info("Music uploaded successfully: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Music uploaded successfully", response));
    }

    /**
     * Get music by ID
     *
     * @param id Music ID
     * @return Music details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get music by ID",
            description = "Retrieve detailed music information by ID")
    public ResponseEntity<ApiResponse<MusicDetailResponse>> getMusicById(@PathVariable Long id) {
        log.debug("Get music by ID: {}", id);

        Long userId = null;
        if (SecurityUtil.isAuthenticated()) {
            String username = SecurityUtil.getCurrentUsername();
            userId = userService.getUserProfile(username).getId();
        }

        MusicDetailResponse response = musicService.getMusicById(id, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update music metadata
     *
     * @param id      Music ID
     * @param request Update request
     * @return Updated music information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update music",
            description = "Update music metadata (owner or admin only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MusicResponse>> updateMusic(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMusicRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Update music request: {} by user: {}", id, username);

        MusicResponse response = musicService.updateMusic(id, request, userId);

        log.info("Music updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Music updated successfully", response));
    }

    /**
     * Delete music
     *
     * @param id Music ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete music",
            description = "Delete a music file (owner or admin only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteMusic(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Delete music request: {} by user: {}", id, username);

        musicService.deleteMusic(id, userId);

        log.info("Music deleted successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Music deleted successfully", null));
    }

    /**
     * Get current user's uploaded music
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sort Sort field
     * @return Page of music
     */
    @GetMapping("/my")
    @Operation(summary = "Get my music",
            description = "Get music uploaded by current user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getMyMusic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.debug("Get my music request from user: {}", username);

        Pageable pageable = createPageable(page, size, sort);
        Page<MusicResponse> response = musicService.getMusicByUploader(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search music
     *
     * @param keyword Search keyword
     * @param page    Page number
     * @param size    Page size
     * @return Page of matching music
     */
    @GetMapping("/search")
    @Operation(summary = "Search music",
            description = "Search music by title or artist name")
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> searchMusic(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Search music with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MusicResponse> response = musicService.searchMusic(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get popular music
     *
     * @param page Page number
     * @param size Page size
     * @return Page of popular music
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular music",
            description = "Get music ordered by play count")
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getPopularMusic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Get popular music request");

        Pageable pageable = PageRequest.of(page, size);
        Page<MusicResponse> response = musicService.getPopularMusic(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get recent music
     *
     * @param page Page number
     * @param size Page size
     * @return Page of recent music
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent music",
            description = "Get recently uploaded music")
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getRecentMusic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Get recent music request");

        Pageable pageable = PageRequest.of(page, size);
        Page<MusicResponse> response = musicService.getRecentMusic(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get music by genre
     *
     * @param genreId Genre ID
     * @param page    Page number
     * @param size    Page size
     * @return Page of music
     */
    @GetMapping("/genre/{genreId}")
    @Operation(summary = "Get music by genre",
            description = "Get all music in a specific genre")
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getMusicByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Get music by genre: {}", genreId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MusicResponse> response = musicService.getMusicByGenre(genreId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all public music
     *
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @return Page of music
     */
    @GetMapping
    @Operation(summary = "Get all public music",
            description = "Get all approved public music")
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getAllPublicMusic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        log.debug("Get all public music request");

        Pageable pageable = createPageable(page, size, sort);
        Page<MusicResponse> response = musicService.getAllPublicMusic(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Stream music file
     *
     * @param id Music ID
     * @return Music file as stream
     */
    @GetMapping("/{id}/stream")
    @Operation(summary = "Stream music",
            description = "Stream music file for playback")
    public ResponseEntity<Resource> streamMusic(@PathVariable Long id) {
        log.debug("Stream music request: {}", id);

        Long userId = null;
        if (SecurityUtil.isAuthenticated()) {
            String username = SecurityUtil.getCurrentUsername();
            userId = userService.getUserProfile(username).getId();
        }

        Resource resource = musicService.streamMusic(id, userId);

        // Determine content type
        String contentType = "audio/mpeg"; // Default to MP3
        try {
            String filename = resource.getFilename();
            if (filename != null) {
                if (filename.endsWith(".flac")) contentType = "audio/flac";
                else if (filename.endsWith(".aac")) contentType = "audio/aac";
                else if (filename.endsWith(".ogg")) contentType = "audio/ogg";
                else if (filename.endsWith(".wav")) contentType = "audio/wav";
            }
        } catch (Exception e) {
            log.warn("Failed to determine content type, using default", e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    /**
     * Record music play
     *
     * @param id Music ID
     * @return Success response
     */
    @PostMapping("/{id}/play")
    @Operation(summary = "Record play",
            description = "Increment play count for a music track")
    public ResponseEntity<ApiResponse<Void>> recordPlay(@PathVariable Long id) {
        log.debug("Record play for music: {}", id);

        musicService.incrementPlayCount(id);

        return ResponseEntity.ok(ApiResponse.success("Play recorded", null));
    }

    /**
     * Create pageable with sorting
     *
     * @param page Page number
     * @param size Page size
     * @param sort Sort string (format: "field,direction")
     * @return Pageable object
     */
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String field = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 &&
                "asc".equalsIgnoreCase(sortParams[1]) ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}
