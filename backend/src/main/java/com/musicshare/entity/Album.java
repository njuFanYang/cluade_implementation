package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Album Entity
 *
 * <p>Represents a music album in the MusicShare platform.
 * Albums can contain multiple music tracks from the same artist.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "albums", indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_artist", columnList = "artist"),
        @Index(name = "idx_creator", columnList = "creator_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Album title
     */
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Album title is required")
    @Size(max = 200, message = "Album title cannot exceed 200 characters")
    private String title;

    /**
     * Album artist name
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Artist name is required")
    @Size(max = 100, message = "Artist name cannot exceed 100 characters")
    private String artist;

    /**
     * Album cover image URL
     */
    @Column(length = 500)
    private String coverImage;

    /**
     * Album description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Album release date
     */
    @Column
    private LocalDate releaseDate;

    /**
     * Creator user ID
     */
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /**
     * Creator user (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;

    /**
     * Number of music tracks in this album
     */
    @Column(nullable = false)
    private Integer musicCount = 0;

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
     * Check if user is the owner of this album
     *
     * @param userId User ID to check
     * @return true if user is the owner
     */
    public boolean isOwnedBy(Long userId) {
        return this.creatorId.equals(userId);
    }
}
