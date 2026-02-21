package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Music Entity
 *
 * <p>Represents a music track in the MusicShare platform.
 * Contains metadata, file information, and statistics for each music track.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "music", indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_artist", columnList = "artist"),
        @Index(name = "idx_genre", columnList = "genre_id"),
        @Index(name = "idx_album", columnList = "album_id"),
        @Index(name = "idx_uploader", columnList = "uploader_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created", columnList = "created_at"),
        @Index(name = "idx_play_count", columnList = "play_count"),
        @Index(name = "idx_public", columnList = "is_public")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Music {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Music title
     */
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Music title is required")
    @Size(max = 200, message = "Music title cannot exceed 200 characters")
    private String title;

    /**
     * Artist name
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Artist name is required")
    @Size(max = 100, message = "Artist name cannot exceed 100 characters")
    private String artist;

    /**
     * Album ID (optional)
     */
    @Column(name = "album_id")
    private Long albumId;

    /**
     * Album (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", insertable = false, updatable = false)
    private Album album;

    /**
     * Genre ID
     */
    @Column(name = "genre_id", nullable = false)
    @NotNull(message = "Genre is required")
    private Long genreId;

    /**
     * Genre (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private Genre genre;

    /**
     * Music duration in seconds
     */
    @Column(nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer duration;

    /**
     * File storage path (relative)
     */
    @Column(nullable = false, unique = true, length = 500)
    @NotBlank(message = "File path is required")
    private String filePath;

    /**
     * Original file name
     */
    @Column(nullable = false, length = 255)
    @NotBlank(message = "File name is required")
    private String fileName;

    /**
     * File size in bytes
     */
    @Column(nullable = false)
    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be positive")
    private Long fileSize;

    /**
     * File format (mp3, flac, aac, etc.)
     */
    @Column(nullable = false, length = 10)
    @NotBlank(message = "File format is required")
    private String fileFormat;

    /**
     * Cover image URL
     */
    @Column(length = 500)
    private String coverImage;

    /**
     * Music description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Lyrics
     */
    @Column(columnDefinition = "TEXT")
    private String lyrics;

    /**
     * Release date
     */
    @Column
    private LocalDate releaseDate;

    /**
     * Uploader user ID
     */
    @Column(name = "uploader_id", nullable = false)
    @NotNull(message = "Uploader ID is required")
    private Long uploaderId;

    /**
     * Uploader user (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", insertable = false, updatable = false)
    private User uploader;

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
     * Comment count
     */
    @Column(nullable = false)
    private Integer commentCount = 0;

    /**
     * Download count
     */
    @Column(nullable = false)
    private Integer downloadCount = 0;

    /**
     * Music approval status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MusicStatus status = MusicStatus.PENDING;

    /**
     * Public visibility flag
     */
    @Column(nullable = false)
    private Boolean isPublic = true;

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
     * Increment comment count
     */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /**
     * Decrement comment count
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /**
     * Increment download count
     */
    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    /**
     * Check if user is the owner of this music
     *
     * @param userId User ID to check
     * @return true if user is the owner
     */
    public boolean isOwnedBy(Long userId) {
        return this.uploaderId.equals(userId);
    }

    /**
     * Check if music is approved
     *
     * @return true if music is approved
     */
    public boolean isApproved() {
        return this.status == MusicStatus.APPROVED;
    }

    /**
     * Check if music is accessible by user
     *
     * @param userId User ID to check (null for anonymous)
     * @return true if accessible
     */
    public boolean isAccessibleBy(Long userId) {
        // Public approved music is accessible to everyone
        if (isPublic && isApproved()) {
            return true;
        }
        // Owner can always access their music
        if (userId != null && isOwnedBy(userId)) {
            return true;
        }
        return false;
    }
}
