package com.musicshare.repository;

import com.musicshare.entity.PlaylistMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PlaylistMusic Repository
 *
 * <p>Data access layer for PlaylistMusic entity. Provides CRUD operations and
 * custom query methods for playlist-music association management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusic, Long> {

    /**
     * Find playlist-music associations by playlist ID ordered by position
     *
     * @param playlistId Playlist ID
     * @return List of playlist-music associations
     */
    List<PlaylistMusic> findByPlaylistIdOrderByPositionAsc(Long playlistId);

    /**
     * Find playlist-music associations with music and genre eagerly loaded
     *
     * @param playlistId Playlist ID
     * @return List of playlist-music associations with relationships
     */
    @Query("SELECT pm FROM PlaylistMusic pm " +
            "LEFT JOIN FETCH pm.music m " +
            "LEFT JOIN FETCH m.genre " +
            "LEFT JOIN FETCH m.uploader " +
            "WHERE pm.playlistId = :playlistId " +
            "ORDER BY pm.position ASC")
    List<PlaylistMusic> findByPlaylistIdWithMusicAndGenre(@Param("playlistId") Long playlistId);

    /**
     * Find playlist-music association by playlist ID and music ID
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     * @return Optional containing playlist-music association
     */
    Optional<PlaylistMusic> findByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    /**
     * Check if music exists in playlist
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     * @return true if exists
     */
    boolean existsByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    /**
     * Delete playlist-music association by playlist ID and music ID
     *
     * @param playlistId Playlist ID
     * @param musicId    Music ID
     */
    @Modifying
    @Query("DELETE FROM PlaylistMusic pm WHERE pm.playlistId = :playlistId AND pm.musicId = :musicId")
    void deleteByPlaylistIdAndMusicId(@Param("playlistId") Long playlistId, @Param("musicId") Long musicId);

    /**
     * Delete all associations for a playlist
     *
     * @param playlistId Playlist ID
     */
    @Modifying
    @Query("DELETE FROM PlaylistMusic pm WHERE pm.playlistId = :playlistId")
    void deleteByPlaylistId(@Param("playlistId") Long playlistId);

    /**
     * Get maximum position in playlist
     *
     * @param playlistId Playlist ID
     * @return Maximum position (null if playlist is empty)
     */
    @Query("SELECT MAX(pm.position) FROM PlaylistMusic pm WHERE pm.playlistId = :playlistId")
    Integer getMaxPosition(@Param("playlistId") Long playlistId);

    /**
     * Count music in playlist
     *
     * @param playlistId Playlist ID
     * @return Count of music
     */
    long countByPlaylistId(Long playlistId);
}
