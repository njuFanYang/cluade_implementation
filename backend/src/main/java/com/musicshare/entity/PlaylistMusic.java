package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * PlaylistMusic Entity
 *
 * <p>Represents the association between playlists and music tracks.
 * Maintains the order of music tracks within a playlist.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "playlist_music",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_playlist_music",
                columnNames = {"playlist_id", "music_id"}
        ),
        indexes = {
                @Index(name = "idx_playlist", columnList = "playlist_id"),
                @Index(name = "idx_music", columnList = "music_id"),
                @Index(name = "idx_position", columnList = "playlist_id,position")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistMusic {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Playlist ID
     */
    @Column(name = "playlist_id", nullable = false)
    @NotNull(message = "Playlist ID is required")
    private Long playlistId;

    /**
     * Playlist (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", insertable = false, updatable = false)
    private Playlist playlist;

    /**
     * Music ID
     */
    @Column(name = "music_id", nullable = false)
    @NotNull(message = "Music ID is required")
    private Long musicId;

    /**
     * Music (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", insertable = false, updatable = false)
    private Music music;

    /**
     * Position in playlist (0-indexed)
     */
    @Column(nullable = false)
    @NotNull(message = "Position is required")
    private Integer position;

    /**
     * Added timestamp
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;
}
