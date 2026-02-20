package com.musicshare.service.impl;

import com.musicshare.entity.Comment;
import com.musicshare.entity.Music;
import com.musicshare.entity.Playlist;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.CommentRepository;
import com.musicshare.repository.MusicRepository;
import com.musicshare.repository.PlaylistRepository;
import com.musicshare.service.CommentService;
import com.musicshare.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment service implementation
 *
 * @author MusicShare Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;

    @Override
    @Transactional
    public Comment createComment(String content, Comment.TargetType targetType, Long targetId, Long parentCommentId) {
        log.info("Creating comment on {} {} by user {}", targetType, targetId, SecurityUtil.getCurrentUserId());

        // Validate target exists
        validateTargetExists(targetType, targetId);

        // Validate parent comment if replying
        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

            // Validate parent comment is on the same target
            if (!parentComment.getTargetType().equals(targetType) ||
                !parentComment.getTargetId().equals(targetId)) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER);
            }
        }

        // Create comment
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(SecurityUtil.getCurrentUserId());
        comment.setTargetType(targetType);
        comment.setTargetId(targetId);
        comment.setParentCommentId(parentCommentId);

        comment = commentRepository.save(comment);

        // Update parent comment reply count
        if (parentComment != null) {
            parentComment.incrementReplyCount();
            commentRepository.save(parentComment);
        }

        // Update target comment count
        updateTargetCommentCount(targetType, targetId, 1);

        log.info("Comment created successfully: {}", comment.getId());
        return comment;
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, String content) {
        log.info("Updating comment {} by user {}", commentId, SecurityUtil.getCurrentUserId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // Check permission
        if (!comment.isOwnedBy(SecurityUtil.getCurrentUserId()) &&
            !SecurityUtil.isAdmin()) {
            throw new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED);
        }

        comment.setContent(content);
        comment = commentRepository.save(comment);

        log.info("Comment updated successfully: {}", commentId);
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Deleting comment {} by user {}", commentId, SecurityUtil.getCurrentUserId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // Check permission
        if (!comment.isOwnedBy(SecurityUtil.getCurrentUserId()) &&
            !SecurityUtil.isAdmin()) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_COMMENT);
        }

        // Update parent comment reply count if this is a reply
        if (comment.getParentCommentId() != null) {
            commentRepository.findById(comment.getParentCommentId()).ifPresent(parent -> {
                parent.decrementReplyCount();
                commentRepository.save(parent);
            });
        }

        // Update target comment count
        updateTargetCommentCount(comment.getTargetType(), comment.getTargetId(), -1);

        // Delete comment (cascades to replies)
        commentRepository.delete(comment);

        log.info("Comment deleted successfully: {}", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByTarget(Comment.TargetType targetType, Long targetId, Pageable pageable) {
        log.debug("Fetching comments for {} {}", targetType, targetId);
        return commentRepository.findByTargetWithUser(targetType, targetId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getReplies(Long parentCommentId, Pageable pageable) {
        log.debug("Fetching replies for comment {}", parentCommentId);

        // Validate parent comment exists
        if (!commentRepository.existsById(parentCommentId)) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }

        return commentRepository.findRepliesByParentId(parentCommentId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getUserComments(Long userId, Pageable pageable) {
        log.debug("Fetching comments for user {}", userId);
        return commentRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCommentsByTarget(Comment.TargetType targetType, Long targetId) {
        return commentRepository.countTopLevelByTarget(targetType, targetId);
    }

    /**
     * Validate that target resource exists
     */
    private void validateTargetExists(Comment.TargetType targetType, Long targetId) {
        boolean exists = switch (targetType) {
            case MUSIC -> musicRepository.existsById(targetId);
            case PLAYLIST -> playlistRepository.existsById(targetId);
        };

        if (!exists) {
            throw new BusinessException(ErrorCode.TARGET_NOT_FOUND);
        }
    }

    /**
     * Update comment count for target resource
     */
    private void updateTargetCommentCount(Comment.TargetType targetType, Long targetId, int delta) {
        switch (targetType) {
            case MUSIC -> {
                Music music = musicRepository.findById(targetId).orElse(null);
                if (music != null) {
                    int newCount = Math.max(0, music.getCommentCount() + delta);
                    music.setCommentCount(newCount);
                    musicRepository.save(music);
                }
            }
            case PLAYLIST -> {
                Playlist playlist = playlistRepository.findById(targetId).orElse(null);
                if (playlist != null) {
                    int newCount = Math.max(0, playlist.getCommentCount() + delta);
                    playlist.setCommentCount(newCount);
                    playlistRepository.save(playlist);
                }
            }
        }
    }
}
