package com.musicshare.repository;

import com.musicshare.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Album Repository
 *
 * <p>Data access layer for Album entity. Provides CRUD operations and custom
 * query methods for album management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    /**
     * Find albums by creator ID
     *
     * @param creatorId Creator user ID
     * @param pageable  Pagination parameters
     * @return Page of albums
     */
    Page<Album> findByCreatorId(Long creatorId, Pageable pageable);

    /**
     * Find albums by creator ID
     *
     * @param creatorId Creator user ID
     * @return List of albums
     */
    List<Album> findByCreatorId(Long creatorId);

    /**
     * Search albums by title or artist
     *
     * @param keyword  Keyword to search
     * @param pageable Pagination parameters
     * @return Page of matching albums
     */
    @Query("SELECT a FROM Album a WHERE a.title LIKE %:keyword% OR a.artist LIKE %:keyword%")
    Page<Album> searchAlbums(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find albums by artist name
     *
     * @param artist   Artist name
     * @param pageable Pagination parameters
     * @return Page of albums
     */
    Page<Album> findByArtistContaining(String artist, Pageable pageable);

    /**
     * Find all albums ordered by creation date descending
     *
     * @param pageable Pagination parameters
     * @return Page of albums
     */
    Page<Album> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Check if album with title and artist exists
     *
     * @param title  Album title
     * @param artist Album artist
     * @return true if exists
     */
    boolean existsByTitleAndArtist(String title, String artist);
}
