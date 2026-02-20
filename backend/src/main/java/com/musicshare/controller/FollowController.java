package com.musicshare.controller;

import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.FollowStatsResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.service.FollowService;
import com.musicshare.service.UserService;
import com.musicshare.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Follow Controller
 *
 * <p>Handles follow-related operations including following/unfollowing users
 * and retrieving follower/following information.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "Follow management APIs")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    /**
     * Follow a user
     *
     * @param userId User ID to follow
     * @return Success response
     */
    @PostMapping("/{userId}")
    @Operation(summary = "Follow user",
            description = "Follow another user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> follow(@PathVariable Long userId) {
        String username = SecurityUtil.getCurrentUsername();
        Long followerId = userService.getUserProfile(username).getId();
        log.info("Follow request from user: {} (ID: {}) to user: {}", username, followerId, userId);

        followService.follow(followerId, userId);

        log.info("Follow successful");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Followed successfully"));
    }

    /**
     * Unfollow a user
     *
     * @param userId User ID to unfollow
     * @return Success response
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Unfollow user",
            description = "Unfollow a user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> unfollow(@PathVariable Long userId) {
        String username = SecurityUtil.getCurrentUsername();
        Long followerId = userService.getUserProfile(username).getId();
        log.info("Unfollow request from user: {} (ID: {}) to user: {}", username, followerId, userId);

        followService.unfollow(followerId, userId);

        log.info("Unfollow successful");
        return ResponseEntity.ok(ApiResponse.success(null, "Unfollowed successfully"));
    }

    /**
     * Check if current user is following another user
     *
     * @param userId User ID to check
     * @return True if following, false otherwise
     */
    @GetMapping("/check/{userId}")
    @Operation(summary = "Check if following",
            description = "Check if current user is following another user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(@PathVariable Long userId) {
        String username = SecurityUtil.getCurrentUsername();
        Long followerId = userService.getUserProfile(username).getId();
        log.debug("Check following status from user: {} to user: {}", followerId, userId);

        boolean isFollowing = followService.isFollowing(followerId, userId);

        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }

    /**
     * Get followers of a user
     *
     * @param userId   User ID
     * @param pageable Pagination parameters
     * @return Page of followers
     */
    @GetMapping("/{userId}/followers")
    @Operation(summary = "Get followers",
            description = "Get list of users following a specific user")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getFollowers(
            @PathVariable Long userId,
            Pageable pageable) {

        log.debug("Get followers for user: {}", userId);

        Page<UserResponse> response = followService.getFollowers(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get users that a user is following
     *
     * @param userId   User ID
     * @param pageable Pagination parameters
     * @return Page of following users
     */
    @GetMapping("/{userId}/following")
    @Operation(summary = "Get following",
            description = "Get list of users that a specific user is following")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getFollowing(
            @PathVariable Long userId,
            Pageable pageable) {

        log.debug("Get following for user: {}", userId);

        Page<UserResponse> response = followService.getFollowing(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get follow statistics for a user
     *
     * @param userId User ID
     * @return Follow statistics (follower and following counts)
     */
    @GetMapping("/{userId}/stats")
    @Operation(summary = "Get follow stats",
            description = "Get follower and following counts for a user")
    public ResponseEntity<ApiResponse<FollowStatsResponse>> getFollowStats(@PathVariable Long userId) {
        log.debug("Get follow stats for user: {}", userId);

        FollowStatsResponse response = followService.getFollowStats(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
