package com.musicshare.controller;

import com.musicshare.dto.response.ApiResponse;
import com.musicshare.entity.Like;
import com.musicshare.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Like Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "Like management APIs")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @Operation(summary = "Like a target", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> like(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        log.info("Like request for {} (ID: {})", targetType, targetId);
        Like.TargetType type = Like.TargetType.valueOf(targetType.toUpperCase());
        likeService.like(type, targetId);

        log.info("Like added successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Liked successfully", null));
    }

    @DeleteMapping
    @Operation(summary = "Unlike a target", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> unlike(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        log.info("Unlike request for {} (ID: {})", targetType, targetId);
        Like.TargetType type = Like.TargetType.valueOf(targetType.toUpperCase());
        likeService.unlike(type, targetId);

        log.info("Like removed successfully");
        return ResponseEntity.ok(ApiResponse.success("Unliked successfully", null));
    }

    @GetMapping("/check")
    @Operation(summary = "Check if liked", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Boolean>> isLiked(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        Like.TargetType type = Like.TargetType.valueOf(targetType.toUpperCase());
        boolean isLiked = likeService.isLiked(type, targetId);
        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }

    @GetMapping("/count")
    @Operation(summary = "Get like count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @RequestParam String targetType,
            @RequestParam Long targetId) {

        Like.TargetType type = Like.TargetType.valueOf(targetType.toUpperCase());
        long count = likeService.getLikeCount(type, targetId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
