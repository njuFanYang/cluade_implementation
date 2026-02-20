package com.musicshare.service;

import com.musicshare.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Comment service interface
 *
 * @description Business logic for comment operations
 * @author MusicShare Team
 * @version 1.0.0
 */
public interface CommentService {

    /**
     * Create a comment
     *
     * @param content Comment content
     * @param targetType Target type
     * @param targetId Target ID
     * @param parentCommentId Parent comment ID (null for top-level)
     * @return Created comment
     */
    Comment createComment(String content, Comment.TargetType targetType, Long targetId, Long parentCommentId);

    /**
     * Update a comment
     *
     * @param commentId Comment ID
     * @param content New content
     * @return Updated comment
     */
    Comment updateComment(Long commentId, String content);

    /**
     * Delete a comment
     *
     * @param commentId Comment ID
     */
    void deleteComment(Long commentId);

    /**
     * Get comment by ID
     *
     * @param commentId Comment ID
     * @return Comment
     */
    Comment getCommentById(Long commentId);

    /**
     * Get comments by target (top-level only)
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @param pageable Pagination info
     * @return Page of comments
     */
    Page<Comment> getCommentsByTarget(Comment.TargetType targetType, Long targetId, Pageable pageable);

    /**
     * Get replies to a comment
     *
     * @param parentCommentId Parent comment ID
     * @param pageable Pagination info
     * @return Page of replies
     */
    Page<Comment> getReplies(Long parentCommentId, Pageable pageable);

    /**
     * Get user's comments
     *
     * @param userId User ID
     * @param pageable Pagination info
     * @return Page of comments
     */
    Page<Comment> getUserComments(Long userId, Pageable pageable);

    /**
     * Count comments by target
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return Comment count
     */
    long countCommentsByTarget(Comment.TargetType targetType, Long targetId);
}
