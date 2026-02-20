package com.musicshare.repository;

import com.musicshare.entity.Music;
import com.musicshare.entity.MusicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Music Repository
 *
 * <p>Data access layer for Music entity. Provides CRUD operations and custom
 * query methods for music management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    /**
     * Find music by ID with genre eagerly loaded
     *
     * @param id Music ID
     * @return Optional containing music with genre
     */
    @Query("SELECT m FROM Music m LEFT JOIN FETCH m.genre WHERE m.id = :id")
    Optional<Music> findByIdWithGenre(@Param("id") Long id);

    /**
     * Find music by ID with all relationships eagerly loaded
     *
     * @param id Music ID
     * @return Optional containing music with all relationships
     */
    @Query("SELECT m FROM Music m " +
            "LEFT JOIN FETCH m.genre " +
            "LEFT JOIN FETCH m.uploader " +
            "LEFT JOIN FETCH m.album " +
            "WHERE m.id = :id")
    Optional<Music> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find music by uploader ID
     *
     * @param uploaderId Uploader user ID
     * @param pageable   Pagination parameters
     * @return Page of music
     */
    Page<Music> findByUploaderId(Long uploaderId, Pageable pageable);

    /**
     * Find music by uploader ID and status
     *
     * @param uploaderId Uploader user ID
     * @param status     Music status
     * @param pageable   Pagination parameters
     * @return Page of music
     */
    Page<Music> findByUploaderIdAndStatus(Long uploaderId, MusicStatus status, Pageable pageable);

    /**
     * Find public approved music by genre ID
     *
     * @param genreId  Genre ID
     * @param pageable Pagination parameters
     * @return Page of music
     */
    @Query("SELECT m FROM Music m WHERE m.genreId = :genreId AND m.isPublic = true AND m.status = 'APPROVED'")
    Page<Music> findByGenreIdPublic(@Param("genreId") Long genreId, Pageable pageable);

    /**
     * Find public approved music by album ID
     *
     * @param albumId  Album ID
     * @param pageable Pagination parameters
     * @return Page of music
     */
    @Query("SELECT m FROM Music m WHERE m.albumId = :albumId AND m.isPublic = true AND m.status = 'APPROVED'")
    Page<Music> findByAlbumIdPublic(@Param("albumId") Long albumId, Pageable pageable);

    /**
     * Search public approved music by title or artist
     *
     * @param keyword  Keyword to search
     * @param pageable Pagination parameters
     * @return Page of matching music
     */
    @Query("SELECT m FROM Music m WHERE (m.title LIKE %:keyword% OR m.artist LIKE %:keyword%) " +
            "AND m.isPublic = true AND m.status = 'APPROVED'")
    Page<Music> searchMusicPublic(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Search music by title or artist (all statuses, for admin/owner)
     *
     * @param keyword  Keyword to search
     * @param pageable Pagination parameters
     * @return Page of matching music
     */
    @Query("SELECT m FROM Music m WHERE m.title LIKE %:keyword% OR m.artist LIKE %:keyword%")
    Page<Music> searchMusic(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find popular public approved music ordered by play count
     *
     * @param pageable Pagination parameters
     * @return Page of popular music
     */
    @Query("SELECT m FROM Music m WHERE m.isPublic = true AND m.status = 'APPROVED' " +
            "ORDER BY m.playCount DESC, m.createdAt DESC")
    Page<Music> findPopularMusic(Pageable pageable);

    /**
     * Find recent public approved music ordered by creation date
     *
     * @param pageable Pagination parameters
     * @return Page of recent music
     */
    @Query("SELECT m FROM Music m WHERE m.isPublic = true AND m.status = 'APPROVED' " +
            "ORDER BY m.createdAt DESC")
    Page<Music> findRecentMusic(Pageable pageable);

    /**
     * Find all public approved music
     *
     * @param pageable Pagination parameters
     * @return Page of music
     */
    @Query("SELECT m FROM Music m WHERE m.isPublic = true AND m.status = 'APPROVED'")
    Page<Music> findAllPublicApproved(Pageable pageable);

    /**
     * Find music by status
     *
     * @param status   Music status
     * @param pageable Pagination parameters
     * @return Page of music
     */
    Page<Music> findByStatus(MusicStatus status, Pageable pageable);

    /**
     * Check if music with file path exists
     *
     * @param filePath File path
     * @return true if exists
     */
    boolean existsByFilePath(String filePath);

    /**
     * Find music by file path
     *
     * @param filePath File path
     * @return Optional containing music
     */
    Optional<Music> findByFilePath(String filePath);

    /**
     * Count music by uploader ID
     *
     * @param uploaderId Uploader user ID
     * @return Count of music
     */
    long countByUploaderId(Long uploaderId);

    /**
     * Count music by genre ID
     *
     * @param genreId Genre ID
     * @return Count of music
     */
    long countByGenreId(Long genreId);

    /**
     * Count music by album ID
     *
     * @param albumId Album ID
     * @return Count of music
     */
    long countByAlbumId(Long albumId);
}
