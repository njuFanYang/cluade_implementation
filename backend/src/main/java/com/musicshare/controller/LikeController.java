package com.musicshare.controller;

import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.LikeResponse;
import com.musicshare.service.LikeService;
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
 * Like Controller
 *
 * <p>Handles like-related operations including liking/unliking targets
 * and retrieving like information.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "Like management APIs")
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    /**
     * Like a target
     *
     * @param targetType Target type (MUSIC, PLAYLIST, COMMENT)
     * @param targetId   Target ID
     * @return Success response
     */
    @PostMapping
    @Operation(summary = "Like a target",
            description = "Add a like to music, playlist, or comment",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> like(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Like request from user: {} for {} (ID: {})", username, targetType, targetId);

        likeService.like(userId, targetType, targetId);

        log.info("Like added successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Liked successfully"));
    }

    /**
     * Unlike a target
     *
     * @param targetType Target type (MUSIC, PLAYLIST, COMMENT)
     * @param targetId   Target ID
     * @return Success response
     */
    @DeleteMapping
    @Operation(summary = "Unlike a target",
            description = "Remove a like from music, playlist, or comment",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> unlike(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Unlike request from user: {} for {} (ID: {})", username, targetType, targetId);

        likeService.unlike(userId, targetType, targetId);

        log.info("Like removed successfully");
        return ResponseEntity.ok(ApiResponse.success(null, "Unliked successfully"));
    }

    /**
     * Check if current user has liked a target
     *
     * @param targetType Target type (MUSIC, PLAYLIST, COMMENT)
     * @param targetId   Target ID
     * @return True if liked, false otherwise
     */
    @GetMapping("/check")
    @Operation(summary = "Check if liked",
            description = "Check if current user has liked a target",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Boolean>> isLiked(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.debug("Check like status for user: {} on {} (ID: {})", username, targetType, targetId);

        boolean isLiked = likeService.isLiked(userId, targetType, targetId);

        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }

    /**
     * Get like count for a target
     *
     * @param targetType Target type (MUSIC, PLAYLIST, COMMENT)
     * @param targetId   Target ID
     * @return Like count
     */
    @GetMapping("/count")
    @Operation(summary = "Get like count",
            description = "Get the number of likes for a target")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        log.debug("Get like count for {} (ID: {})", targetType, targetId);

        long count = likeService.getLikeCount(targetType, targetId);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * Get current user's likes
     *
     * @param targetType Target type filter (MUSIC, PLAYLIST, COMMENT)
     * @param pageable   Pagination parameters
     * @return Page of liked items
     */
    @GetMapping("/my")
    @Operation(summary = "Get my likes",
            description = "Get items liked by current user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<LikeResponse>>> getMyLikes(
            @RequestParam String targetType,
            Pageable pageable) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.debug("Get my likes request from user: {} for type: {}", username, targetType);

        Page<LikeResponse> response = likeService.getLikesByUser(userId, targetType, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
