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
 * Playlist Entity
 *
 * <p>Represents a music playlist in the MusicShare platform.
 * Playlists can contain multiple music tracks and can be public or private.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "playlists", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_creator", columnList = "creator_id"),
        @Index(name = "idx_public", columnList = "is_public"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Playlist name
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Playlist name is required")
    @Size(max = 100, message = "Playlist name cannot exceed 100 characters")
    private String name;

    /**
     * Playlist description
     */
    @Column(length = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Cover image URL
     */
    @Column(length = 500)
    private String coverImage;

    /**
     * Creator user ID
     */
    @Column(nullable = false)
    @NotNull(message = "Creator ID is required")
    private Long creatorId;

    /**
     * Creator user (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;

    /**
     * Public visibility flag
     */
    @Column(nullable = false)
    private Boolean isPublic = true;

    /**
     * Number of music tracks in playlist
     */
    @Column(nullable = false)
    private Integer musicCount = 0;

    /**
     * Play count
     */
    @Column(nullable = false)
    private Integer playCount = 0;

    /**
     * Like count
     */
    @Column(nullable = false)
    private Integer likeCount = 0;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Increment music count
     */
    public void incrementMusicCount() {
        this.musicCount++;
    }

    /**
     * Decrement music count
     */
    public void decrementMusicCount() {
        if (this.musicCount > 0) {
            this.musicCount--;
        }
    }

    /**
     * Increment play count
     */
    public void incrementPlayCount() {
        this.playCount++;
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
     * Check if user is the owner of this playlist
     *
     * @param userId User ID to check
     * @return true if user is the owner
     */
    public boolean isOwnedBy(Long userId) {
        return this.creatorId.equals(userId);
    }

    /**
     * Check if playlist is accessible by user
     *
     * @param userId User ID to check (null for anonymous)
     * @return true if accessible
     */
    public boolean isAccessibleBy(Long userId) {
        // Public playlists are accessible to everyone
        if (isPublic) {
            return true;
        }
        // Owner can always access their private playlists
        if (userId != null && isOwnedBy(userId)) {
            return true;
        }
        return false;
    }
}
