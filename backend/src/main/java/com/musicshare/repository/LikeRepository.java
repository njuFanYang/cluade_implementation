package com.musicshare.repository;

import com.musicshare.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Like repository interface
 *
 * @description Data access layer for Like entity
 * @author MusicShare Team
 * @version 1.0.0
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * Find like by user and target
     *
     * @param userId User ID
     * @param targetType Target type
     * @param targetId Target ID
     * @return Optional like
     */
    Optional<Like> findByUserIdAndTargetTypeAndTargetId(
            Long userId,
            Like.TargetType targetType,
            Long targetId
    );

    /**
     * Check if user has liked target
     *
     * @param userId User ID
     * @param targetType Target type
     * @param targetId Target ID
     * @return True if liked
     */
    boolean existsByUserIdAndTargetTypeAndTargetId(
            Long userId,
            Like.TargetType targetType,
            Long targetId
    );

    /**
     * Count likes by target
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return Like count
     */
    long countByTargetTypeAndTargetId(
            Like.TargetType targetType,
            Long targetId
    );

    /**
     * Find user's likes by type
     *
     * @param userId User ID
     * @param targetType Target type
     * @param pageable Pagination info
     * @return Page of likes
     */
    Page<Like> findByUserIdAndTargetType(
            Long userId,
            Like.TargetType targetType,
            Pageable pageable
    );

    /**
     * Find all user's likes
     *
     * @param userId User ID
     * @param pageable Pagination info
     * @return Page of likes
     */
    Page<Like> findByUserId(Long userId, Pageable pageable);

    /**
     * Find users who liked a target
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @param pageable Pagination info
     * @return Page of likes with user info
     */
    @Query("SELECT l FROM Like l " +
           "LEFT JOIN FETCH l.user " +
           "WHERE l.targetType = :targetType " +
           "AND l.targetId = :targetId")
    Page<Like> findByTargetWithUser(
            @Param("targetType") Like.TargetType targetType,
            @Param("targetId") Long targetId,
            Pageable pageable
    );

    /**
     * Delete like by user and target
     *
     * @param userId User ID
     * @param targetType Target type
     * @param targetId Target ID
     */
    void deleteByUserIdAndTargetTypeAndTargetId(
            Long userId,
            Like.TargetType targetType,
            Long targetId
    );

    /**
     * Delete likes by target
     *
     * @param targetType Target type
     * @param targetId Target ID
     */
    void deleteByTargetTypeAndTargetId(
            Like.TargetType targetType,
            Long targetId
    );

    /**
     * Delete likes by user
     *
     * @param userId User ID
     */
    void deleteByUserId(Long userId);
}
