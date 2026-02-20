package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Comment entity
 *
 * @description Represents a comment on music or playlist
 * @author MusicShare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_target", columnList = "target_type, target_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_parent", columnList = "parent_comment_id"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Comment content
     */
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 1000, message = "Comment content cannot exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String content;

    /**
     * Commenter
     */
    @NotNull(message = "User ID cannot be null")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * Target type (MUSIC, PLAYLIST)
     */
    @NotNull(message = "Target type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    /**
     * Target ID (music_id or playlist_id)
     */
    @NotNull(message = "Target ID cannot be null")
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /**
     * Parent comment ID (for replies)
     * Null for top-level comments
     */
    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", insertable = false, updatable = false)
    private Comment parentComment;

    /**
     * Number of likes
     */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    /**
     * Number of replies
     */
    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Update timestamp
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Target type enumeration
     */
    public enum TargetType {
        MUSIC,
        PLAYLIST
    }

    /**
     * Check if comment is owned by user
     */
    public boolean isOwnedBy(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * Check if comment is a reply
     */
    public boolean isReply() {
        return this.parentCommentId != null;
    }

    /**
     * Increment like count
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * Decrement like count
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * Increment reply count
     */
    public void incrementReplyCount() {
        this.replyCount++;
    }

    /**
     * Decrement reply count
     */
    public void decrementReplyCount() {
        if (this.replyCount > 0) {
            this.replyCount--;
        }
    }
}
