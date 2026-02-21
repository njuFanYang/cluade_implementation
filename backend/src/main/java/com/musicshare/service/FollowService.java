package com.musicshare.service;

import com.musicshare.dto.response.FollowStatsResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Follow service interface
 *
 * @description Business logic for follow operations
 * @author MusicShare Team
 * @version 1.0.0
 */
public interface FollowService {

    /**
     * Follow a user
     *
     * @param followingId User ID to follow
     * @return Created follow
     */
    Follow follow(Long followingId);

    /**
     * Unfollow a user
     *
     * @param followingId User ID to unfollow
     */
    void unfollow(Long followingId);

    /**
     * Check if current user is following another user
     *
     * @param followingId User ID to check
     * @return True if following
     */
    boolean isFollowing(Long followingId);

    /**
     * Get followers of a user
     *
     * @param userId User ID
     * @param pageable Pagination info
     * @return Page of follower UserResponses
     */
    Page<UserResponse> getFollowers(Long userId, Pageable pageable);

    /**
     * Get users that a user is following
     *
     * @param userId User ID
     * @param pageable Pagination info
     * @return Page of following UserResponses
     */
    Page<UserResponse> getFollowing(Long userId, Pageable pageable);

    /**
     * Get follower count
     *
     * @param userId User ID
     * @return Follower count
     */
    long getFollowerCount(Long userId);

    /**
     * Get following count
     *
     * @param userId User ID
     * @return Following count
     */
    long getFollowingCount(Long userId);

    /**
     * Get follow statistics for a user
     *
     * @param userId User ID
     * @return Follow statistics
     */
    FollowStatsResponse getFollowStats(Long userId);
}
