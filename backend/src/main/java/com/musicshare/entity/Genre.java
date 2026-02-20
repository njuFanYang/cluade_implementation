package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Genre Entity
 *
 * <p>Represents a music genre/category in the MusicShare platform.
 * Genres are used to classify music into different types (Pop, Rock, Jazz, etc.)
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "genres", indexes = {
        @Index(name = "idx_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Genre name in English (unique)
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Genre name is required")
    @Size(max = 50, message = "Genre name cannot exceed 50 characters")
    private String name;

    /**
     * Genre name in Chinese
     */
    @Column(length = 50)
    @Size(max = 50, message = "Chinese name cannot exceed 50 characters")
    private String nameZh;

    /**
     * Genre description
     */
    @Column(length = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Icon URL or identifier
     */
    @Column(length = 255)
    private String icon;

    /**
     * Number of music tracks in this genre
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
}
