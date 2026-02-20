package com.musicshare.controller;

import com.musicshare.dto.request.CreatePlaylistRequest;
import com.musicshare.dto.request.UpdatePlaylistRequest;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.PlaylistDetailResponse;
import com.musicshare.dto.response.PlaylistResponse;
import com.musicshare.service.PlaylistService;
import com.musicshare.service.UserService;
import com.musicshare.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Playlist Controller
 *
 * <p>Handles playlist-related operations including CRUD and music management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
@Tag(name = "Playlist", description = "Playlist management APIs")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final UserService userService;

    /**
     * Create a new playlist
     *
     * @param request Playlist creation request
     * @return Created playlist
     */
    @PostMapping
    @Operation(summary = "Create playlist",
            description = "Create a new playlist",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PlaylistResponse>> createPlaylist(
            @Valid @RequestBody CreatePlaylistRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Create playlist request from user: {} (ID: {})", username, userId);

        PlaylistResponse response = playlistService.createPlaylist(request, userId);

        log.info("Playlist created successfully: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Playlist created successfully"));
    }

    /**
     * Get playlist details
     *
     * @param id Playlist ID
     * @return Playlist details with music list
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get playlist details",
            description = "Retrieve playlist details including music list")
    public ResponseEntity<ApiResponse<PlaylistDetailResponse>> getPlaylistDetail(@PathVariable Long id) {
        log.debug("Get playlist detail: {}", id);

        Long userId = null;
        if (SecurityUtil.isAuthenticated()) {
            String username = SecurityUtil.getCurrentUsername();
            userId = userService.getUserProfile(username).getId();
        }

        PlaylistDetailResponse response = playlistService.getPlaylistDetail(id, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update playlist
     *
     * @param id      Playlist ID
     * @param request Update request
     * @return Updated playlist
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update playlist",
            description = "Update playlist information (owner only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PlaylistResponse>> updatePlaylist(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlaylistRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Update playlist request: {} by user: {}", id, username);

        PlaylistResponse response = playlistService.updatePlaylist(id, request, userId);

        log.info("Playlist updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success(response, "Playlist updated successfully"));
    }

    /**
     * Delete playlist
     *
     * @param id Playlist ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete playlist",
            description = "Delete a playlist (owner only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deletePlaylist(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Delete playlist request: {} by user: {}", id, username);

        playlistService.deletePlaylist(id, userId);

        log.info("Playlist deleted successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success(null, "Playlist deleted successfully"));
    }

    /**
     * Get current user's playlists
     *
     * @param page Page number
     * @param size Page size
     * @return Page of playlists
     */
    @GetMapping("/my")
    @Operation(summary = "Get my playlists",
            description = "Get playlists created by current user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<PlaylistResponse>>> getMyPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.debug("Get my playlists request from user: {}", username);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PlaylistResponse> response = playlistService.getPlaylistsByCreator(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get public playlists
     *
     * @param page Page number
     * @param size Page size
     * @return Page of public playlists
     */
    @GetMapping("/public")
    @Operation(summary = "Get public playlists",
            description = "Get all public playlists")
    public ResponseEntity<ApiResponse<Page<PlaylistResponse>>> getPublicPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Get public playlists request");

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PlaylistResponse> response = playlistService.getPublicPlaylists(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search playlists
     *
     * @param keyword Search keyword
     * @param page    Page number
     * @param size    Page size
     * @return Page of matching playlists
     */
    @GetMapping("/search")
    @Operation(summary = "Search playlists",
            description = "Search public playlists by name")
    public ResponseEntity<ApiResponse<Page<PlaylistResponse>>> searchPlaylists(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Search playlists with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PlaylistResponse> response = playlistService.searchPlaylists(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get popular playlists
     *
     * @param page Page number
     * @param size Page size
     * @return Page of popular playlists
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular playlists",
            description = "Get playlists ordered by play count")
    public ResponseEntity<ApiResponse<Page<PlaylistResponse>>> getPopularPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Get popular playlists request");

        Pageable pageable = PageRequest.of(page, size);
        Page<PlaylistResponse> response = playlistService.getPopularPlaylists(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Add music to playlist
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     * @return Success response
     */
    @PostMapping("/{playlistId}/music/{musicId}")
    @Operation(summary = "Add music to playlist",
            description = "Add a music track to playlist (owner only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> addMusicToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long musicId) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Add music {} to playlist {} by user: {}", musicId, playlistId, username);

        playlistService.addMusicToPlaylist(playlistId, musicId, userId);

        log.info("Music added to playlist successfully");
        return ResponseEntity.ok(ApiResponse.success(null, "Music added to playlist"));
    }

    /**
     * Remove music from playlist
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     * @return Success response
     */
    @DeleteMapping("/{playlistId}/music/{musicId}")
    @Operation(summary = "Remove music from playlist",
            description = "Remove a music track from playlist (owner only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> removeMusicFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long musicId) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Remove music {} from playlist {} by user: {}", musicId, playlistId, username);

        playlistService.removeMusicFromPlaylist(playlistId, musicId, userId);

        log.info("Music removed from playlist successfully");
        return ResponseEntity.ok(ApiResponse.success(null, "Music removed from playlist"));
    }

    /**
     * Reorder playlist music
     *
     * @param playlistId Playlist ID
     * @param musicIds   Ordered list of music IDs
     * @return Success response
     */
    @PutMapping("/{playlistId}/reorder")
    @Operation(summary = "Reorder playlist music",
            description = "Change the order of music tracks in playlist (owner only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> reorderPlaylistMusic(
            @PathVariable Long playlistId,
            @RequestBody List<Long> musicIds) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Reorder playlist {} by user: {}", playlistId, username);

        playlistService.reorderPlaylistMusic(playlistId, musicIds, userId);

        log.info("Playlist reordered successfully");
        return ResponseEntity.ok(ApiResponse.success(null, "Playlist reordered successfully"));
    }
}
