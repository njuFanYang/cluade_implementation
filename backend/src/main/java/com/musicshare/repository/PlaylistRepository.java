package com.musicshare.repository;

import com.musicshare.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Playlist Repository
 *
 * <p>Data access layer for Playlist entity. Provides CRUD operations and custom
 * query methods for playlist management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    /**
     * Find playlists by creator ID
     *
     * @param creatorId Creator user ID
     * @param pageable  Pagination parameters
     * @return Page of playlists
     */
    Page<Playlist> findByCreatorId(Long creatorId, Pageable pageable);

    /**
     * Find playlists by creator ID
     *
     * @param creatorId Creator user ID
     * @return List of playlists
     */
    List<Playlist> findByCreatorId(Long creatorId);

    /**
     * Find all public playlists
     *
     * @param pageable Pagination parameters
     * @return Page of public playlists
     */
    Page<Playlist> findByIsPublicTrue(Pageable pageable);

    /**
     * Search public playlists by name
     *
     * @param keyword  Search keyword
     * @param pageable Pagination parameters
     * @return Page of matching playlists
     */
    @Query("SELECT p FROM Playlist p WHERE p.name LIKE %:keyword% AND p.isPublic = true")
    Page<Playlist> searchPublicPlaylists(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find playlist by ID with creator eagerly loaded
     *
     * @param id Playlist ID
     * @return Optional containing playlist with creator
     */
    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.creator WHERE p.id = :id")
    Optional<Playlist> findByIdWithCreator(@Param("id") Long id);

    /**
     * Find popular public playlists ordered by play count
     *
     * @param pageable Pagination parameters
     * @return Page of popular playlists
     */
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true ORDER BY p.playCount DESC, p.createdAt DESC")
    Page<Playlist> findPopularPlaylists(Pageable pageable);

    /**
     * Count playlists by creator ID
     *
     * @param creatorId Creator user ID
     * @return Count of playlists
     */
    long countByCreatorId(Long creatorId);
}
