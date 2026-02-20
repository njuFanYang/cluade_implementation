package com.musicshare.controller;

import com.musicshare.dto.request.CreateCommentRequest;
import com.musicshare.dto.request.UpdateCommentRequest;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.CommentResponse;
import com.musicshare.service.CommentService;
import com.musicshare.service.UserService;
import com.musicshare.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Comment Controller
 *
 * <p>Handles comment-related operations including create, update, delete,
 * and retrieval of comments for various target types.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment management APIs")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    /**
     * Create a new comment
     *
     * @param request Comment creation request
     * @return Created comment
     */
    @PostMapping
    @Operation(summary = "Create comment",
            description = "Create a new comment on music, playlist, or another comment",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Valid @RequestBody CreateCommentRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Create comment request from user: {} (ID: {})", username, userId);

        CommentResponse response = commentService.createComment(request, userId);

        log.info("Comment created successfully: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Comment created successfully"));
    }

    /**
     * Update a comment
     *
     * @param id      Comment ID
     * @param request Update request
     * @return Updated comment
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update comment",
            description = "Update comment content (owner only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Update comment request: {} by user: {}", id, username);

        CommentResponse response = commentService.updateComment(id, request, userId);

        log.info("Comment updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success(response, "Comment updated successfully"));
    }

    /**
     * Delete a comment
     *
     * @param id Comment ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment",
            description = "Delete a comment (owner or admin only)",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Delete comment request: {} by user: {}", id, username);

        commentService.deleteComment(id, userId);

        log.info("Comment deleted successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted successfully"));
    }

    /**
     * Get comment by ID
     *
     * @param id Comment ID
     * @return Comment details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID",
            description = "Retrieve comment details by ID")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long id) {
        log.debug("Get comment by ID: {}", id);

        CommentResponse response = commentService.getCommentById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get comments by target
     *
     * @param targetType Target type (MUSIC, PLAYLIST)
     * @param targetId   Target ID
     * @param pageable   Pagination parameters
     * @return Page of comments
     */
    @GetMapping("/target")
    @Operation(summary = "Get comments by target",
            description = "Get all comments for a specific target (music, playlist, etc.)")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByTarget(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            Pageable pageable) {

        log.debug("Get comments for target: {} (ID: {})", targetType, targetId);

        Page<CommentResponse> response = commentService.getCommentsByTarget(targetType, targetId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get replies to a comment
     *
     * @param id       Parent comment ID
     * @param pageable Pagination parameters
     * @return Page of reply comments
     */
    @GetMapping("/{id}/replies")
    @Operation(summary = "Get comment replies",
            description = "Get all replies to a specific comment")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getReplies(
            @PathVariable Long id,
            Pageable pageable) {

        log.debug("Get replies for comment: {}", id);

        Page<CommentResponse> response = commentService.getReplies(id, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get current user's comments
     *
     * @param pageable Pagination parameters
     * @return Page of user's comments
     */
    @GetMapping("/my")
    @Operation(summary = "Get my comments",
            description = "Get comments created by current user",
            security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getMyComments(Pageable pageable) {
        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.debug("Get my comments request from user: {}", username);

        Page<CommentResponse> response = commentService.getCommentsByUser(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
