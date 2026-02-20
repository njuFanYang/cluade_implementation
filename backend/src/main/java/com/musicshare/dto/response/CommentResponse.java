package com.musicshare.dto.response;

import com.musicshare.entity.Comment;
import com.musicshare.entity.Comment.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Comment Response DTO
 *
 * <p>Data transfer object for comment information in API responses.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userAvatar;
    private TargetType targetType;
    private Long targetId;
    private Long parentCommentId;
    private Integer likeCount;
    private Integer replyCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Create CommentResponse from Comment entity
     *
     * @param comment Comment entity
     * @return CommentResponse instance
     */
    public static CommentResponse fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getUserId());
        response.setTargetType(comment.getTargetType());
        response.setTargetId(comment.getTargetId());
        response.setParentCommentId(comment.getParentCommentId());
        response.setLikeCount(comment.getLikeCount());
        response.setReplyCount(comment.getReplyCount());
        response.setIsLiked(false); // Default value, should be set by service based on current user
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        // Set user information if available
        if (comment.getUser() != null) {
            response.setUsername(comment.getUser().getUsername());
            response.setUserAvatar(comment.getUser().getAvatar());
        }

        return response;
    }

    /**
     * Create CommentResponse from Comment entity with isLiked status
     *
     * @param comment Comment entity
     * @param isLiked Whether the comment is liked by current user
     * @return CommentResponse instance
     */
    public static CommentResponse fromEntity(Comment comment, boolean isLiked) {
        CommentResponse response = fromEntity(comment);
        if (response != null) {
            response.setIsLiked(isLiked);
        }
        return response;
    }
}
