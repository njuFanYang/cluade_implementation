package com.musicshare.service;

import com.musicshare.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Like service interface
 *
 * @description Business logic for like operations
 * @author MusicShare Team
 * @version 1.0.0
 */
public interface LikeService {

    /**
     * Like a target (music, playlist, or comment)
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return Created like
     */
    Like like(Like.TargetType targetType, Long targetId);

    /**
     * Unlike a target
     *
     * @param targetType Target type
     * @param targetId Target ID
     */
    void unlike(Like.TargetType targetType, Long targetId);

    /**
     * Check if current user has liked target
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return True if liked
     */
    boolean isLiked(Like.TargetType targetType, Long targetId);

    /**
     * Get like count for target
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return Like count
     */
    long getLikeCount(Like.TargetType targetType, Long targetId);

    /**
     * Get user's likes by type
     *
     * @param userId User ID
     * @param targetType Target type
     * @param pageable Pagination info
     * @return Page of likes
     */
    Page<Like> getUserLikes(Long userId, Like.TargetType targetType, Pageable pageable);
}
