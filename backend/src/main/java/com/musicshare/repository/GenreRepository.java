package com.musicshare.repository;

import com.musicshare.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Genre Repository
 *
 * <p>Data access layer for Genre entity. Provides CRUD operations and custom
 * query methods for music genre management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Find genre by name
     *
     * @param name Genre name to search
     * @return Optional containing genre if found
     */
    Optional<Genre> findByName(String name);

    /**
     * Check if genre name exists
     *
     * @param name Genre name to check
     * @return true if genre name exists
     */
    boolean existsByName(String name);

    /**
     * Find all genres ordered by music count descending
     *
     * @return List of genres sorted by popularity
     */
    List<Genre> findAllByOrderByMusicCountDesc();

    /**
     * Find all genres ordered by name ascending
     *
     * @return List of genres sorted alphabetically
     */
    List<Genre> findAllByOrderByNameAsc();
}
