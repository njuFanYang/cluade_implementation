package com.musicshare.controller;

import com.musicshare.dto.request.CreateCommentRequest;
import com.musicshare.dto.request.UpdateCommentRequest;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.CommentResponse;
import com.musicshare.entity.Comment;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Comment Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment management APIs")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create comment", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Valid @RequestBody CreateCommentRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.info("Create comment request from user: {} (ID: {})", username, userId);

        Comment comment = commentService.createComment(
                request.getContent(),
                request.getTargetType(),
                request.getTargetId(),
                request.getParentCommentId());
        CommentResponse response = CommentResponse.fromEntity(comment);

        log.info("Comment created successfully: {}", comment.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request) {

        String username = SecurityUtil.getCurrentUsername();
        log.info("Update comment request: {} by user: {}", id, username);

        Comment comment = commentService.updateComment(id, request.getContent());
        CommentResponse response = CommentResponse.fromEntity(comment);

        log.info("Comment updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername();
        log.info("Delete comment request: {} by user: {}", id, username);

        commentService.deleteComment(id);

        log.info("Comment deleted successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long id) {
        log.debug("Get comment by ID: {}", id);
        CommentResponse response = CommentResponse.fromEntity(commentService.getCommentById(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/target")
    @Operation(summary = "Get comments by target")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByTarget(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            Pageable pageable) {

        log.debug("Get comments for target: {} (ID: {})", targetType, targetId);
        Comment.TargetType type = Comment.TargetType.valueOf(targetType.toUpperCase());
        Page<CommentResponse> response = commentService.getCommentsByTarget(type, targetId, pageable)
                .map(CommentResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "Get comment replies")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getReplies(
            @PathVariable Long id,
            Pageable pageable) {

        log.debug("Get replies for comment: {}", id);
        Page<CommentResponse> response = commentService.getReplies(id, pageable)
                .map(CommentResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my comments", security = @SecurityRequirement(name = "JWT"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getMyComments(Pageable pageable) {
        String username = SecurityUtil.getCurrentUsername();
        Long userId = userService.getUserProfile(username).getId();
        log.debug("Get my comments request from user: {}", username);

        Page<CommentResponse> response = commentService.getUserComments(userId, pageable)
                .map(CommentResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
