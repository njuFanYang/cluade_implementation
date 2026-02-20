package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Follow entity
 *
 * @description Represents a follow relationship between users
 * @author MusicShare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "follows",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_follower_following", columnNames = {"follower_id", "following_id"})
       },
       indexes = {
           @Index(name = "idx_follower", columnList = "follower_id"),
           @Index(name = "idx_following", columnList = "following_id"),
           @Index(name = "idx_created", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Follower user ID (the user who follows)
     */
    @NotNull(message = "Follower ID cannot be null")
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private User follower;

    /**
     * Following user ID (the user being followed)
     */
    @NotNull(message = "Following ID cannot be null")
    @Column(name = "following_id", nullable = false)
    private Long followingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", insertable = false, updatable = false)
    private User following;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor for creating a new follow
     */
    public Follow(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    /**
     * Check if follow is created by user
     */
    public boolean isCreatedBy(Long userId) {
        return this.followerId != null && this.followerId.equals(userId);
    }
}
