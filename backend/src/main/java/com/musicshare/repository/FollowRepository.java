package com.musicshare.repository;

import com.musicshare.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Follow repository interface
 *
 * @description Data access layer for Follow entity
 * @author MusicShare Team
 * @version 1.0.0
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * Find follow relationship
     *
     * @param followerId Follower user ID
     * @param followingId Following user ID
     * @return Optional follow
     */
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Check if user is following another user
     *
     * @param followerId Follower user ID
     * @param followingId Following user ID
     * @return True if following
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Get followers of a user (users who follow this user)
     *
     * @param followingId The user being followed
     * @param pageable Pagination info
     * @return Page of follows
     */
    @Query("SELECT f FROM Follow f " +
           "LEFT JOIN FETCH f.follower " +
           "WHERE f.followingId = :followingId")
    Page<Follow> findFollowersByFollowingId(
            @Param("followingId") Long followingId,
            Pageable pageable
    );

    /**
     * Get users that a user is following
     *
     * @param followerId The follower user ID
     * @param pageable Pagination info
     * @return Page of follows
     */
    @Query("SELECT f FROM Follow f " +
           "LEFT JOIN FETCH f.following " +
           "WHERE f.followerId = :followerId")
    Page<Follow> findFollowingByFollowerId(
            @Param("followerId") Long followerId,
            Pageable pageable
    );

    /**
     * Count followers of a user
     *
     * @param followingId User ID being followed
     * @return Follower count
     */
    long countByFollowingId(Long followingId);

    /**
     * Count users that a user is following
     *
     * @param followerId Follower user ID
     * @return Following count
     */
    long countByFollowerId(Long followerId);

    /**
     * Delete follow relationship
     *
     * @param followerId Follower user ID
     * @param followingId Following user ID
     */
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Delete all follows by a user (as follower)
     *
     * @param followerId Follower user ID
     */
    void deleteByFollowerId(Long followerId);

    /**
     * Delete all follows to a user (as following)
     *
     * @param followingId Following user ID
     */
    void deleteByFollowingId(Long followingId);
}
