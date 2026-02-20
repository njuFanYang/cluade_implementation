package com.musicshare.service;

import com.musicshare.dto.request.CreatePlaylistRequest;
import com.musicshare.dto.request.UpdatePlaylistRequest;
import com.musicshare.dto.response.PlaylistDetailResponse;
import com.musicshare.dto.response.PlaylistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Playlist Service Interface
 *
 * <p>Defines business logic operations for playlist management, including
 * CRUD operations, music management, and searching.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public interface PlaylistService {

    /**
     * Create a new playlist
     *
     * @param request   Playlist creation request
     * @param creatorId Creator user ID
     * @return PlaylistResponse containing created playlist information
     * @throws com.musicshare.exception.BusinessException if creation fails
     */
    PlaylistResponse createPlaylist(CreatePlaylistRequest request, Long creatorId);

    /**
     * Get playlist detail by ID
     *
     * @param id     Playlist ID
     * @param userId Current user ID (null if anonymous)
     * @return PlaylistDetailResponse containing playlist details and music list
     * @throws com.musicshare.exception.BusinessException if playlist not found or no permission
     */
    PlaylistDetailResponse getPlaylistDetail(Long id, Long userId);

    /**
     * Update playlist information
     *
     * @param id      Playlist ID
     * @param request Update request
     * @param userId  Current user ID
     * @return PlaylistResponse containing updated playlist information
     * @throws com.musicshare.exception.BusinessException if playlist not found or no permission
     */
    PlaylistResponse updatePlaylist(Long id, UpdatePlaylistRequest request, Long userId);

    /**
     * Delete playlist
     *
     * @param id     Playlist ID
     * @param userId Current user ID
     * @throws com.musicshare.exception.BusinessException if playlist not found or no permission
     */
    void deletePlaylist(Long id, Long userId);

    /**
     * Add music to playlist
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     * @param userId     Current user ID
     * @throws com.musicshare.exception.BusinessException if music already in playlist or no permission
     */
    void addMusicToPlaylist(Long playlistId, Long musicId, Long userId);

    /**
     * Remove music from playlist
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     * @param userId     Current user ID
     * @throws com.musicshare.exception.BusinessException if music not in playlist or no permission
     */
    void removeMusicFromPlaylist(Long playlistId, Long musicId, Long userId);

    /**
     * Reorder music in playlist
     *
     * @param playlistId Playlist ID
     * @param musicIds   Ordered list of music IDs
     * @param userId     Current user ID
     * @throws com.musicshare.exception.BusinessException if invalid order or no permission
     */
    void reorderPlaylistMusic(Long playlistId, List<Long> musicIds, Long userId);

    /**
     * Get playlists by creator
     *
     * @param creatorId Creator user ID
     * @param pageable  Pagination parameters
     * @return Page of playlists
     */
    Page<PlaylistResponse> getPlaylistsByCreator(Long creatorId, Pageable pageable);

    /**
     * Get public playlists
     *
     * @param pageable Pagination parameters
     * @return Page of public playlists
     */
    Page<PlaylistResponse> getPublicPlaylists(Pageable pageable);

    /**
     * Search public playlists
     *
     * @param keyword  Search keyword
     * @param pageable Pagination parameters
     * @return Page of matching playlists
     */
    Page<PlaylistResponse> searchPlaylists(String keyword, Pageable pageable);

    /**
     * Get popular playlists
     *
     * @param pageable Pagination parameters
     * @return Page of popular playlists
     */
    Page<PlaylistResponse> getPopularPlaylists(Pageable pageable);

    /**
     * Check if user can access playlist
     *
     * @param playlistId Playlist ID
     * @param userId     User ID (null if anonymous)
     * @return true if user can access
     */
    boolean canUserAccessPlaylist(Long playlistId, Long userId);

    /**
     * Check if user owns playlist
     *
     * @param playlistId Playlist ID
     * @param userId     User ID
     * @return true if user owns the playlist
     */
    boolean isUserOwner(Long playlistId, Long userId);
}
