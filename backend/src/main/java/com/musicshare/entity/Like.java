package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Like entity
 *
 * @description Represents a like on music, playlist, or comment
 * @author MusicShare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "likes",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_target", columnNames = {"user_id", "target_type", "target_id"})
       },
       indexes = {
           @Index(name = "idx_target", columnList = "target_type, target_id"),
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_created", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who liked
     */
    @NotNull(message = "User ID cannot be null")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * Target type (MUSIC, PLAYLIST, COMMENT)
     */
    @NotNull(message = "Target type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    /**
     * Target ID (music_id, playlist_id, or comment_id)
     */
    @NotNull(message = "Target ID cannot be null")
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Target type enumeration
     */
    public enum TargetType {
        MUSIC,
        PLAYLIST,
        COMMENT
    }

    /**
     * Constructor for creating a new like
     */
    public Like(Long userId, TargetType targetType, Long targetId) {
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    /**
     * Check if like is owned by user
     */
    public boolean isOwnedBy(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }
}
