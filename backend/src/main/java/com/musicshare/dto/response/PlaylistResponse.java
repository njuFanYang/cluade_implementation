package com.musicshare.dto.response;

import com.musicshare.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Playlist Response DTO
 *
 * <p>Data transfer object for playlist information in API responses.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {

    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Long creatorId;
    private String creatorName;
    private Boolean isPublic;
    private Integer musicCount;
    private Integer playCount;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Create PlaylistResponse from Playlist entity
     *
     * @param playlist Playlist entity
     * @return PlaylistResponse instance
     */
    public static PlaylistResponse fromEntity(Playlist playlist) {
        if (playlist == null) {
            return null;
        }

        PlaylistResponse response = new PlaylistResponse();
        response.setId(playlist.getId());
        response.setName(playlist.getName());
        response.setDescription(playlist.getDescription());
        response.setCoverImage(playlist.getCoverImage());
        response.setCreatorId(playlist.getCreatorId());
        response.setIsPublic(playlist.getIsPublic());
        response.setMusicCount(playlist.getMusicCount());
        response.setPlayCount(playlist.getPlayCount());
        response.setLikeCount(playlist.getLikeCount());
        response.setCreatedAt(playlist.getCreatedAt());
        response.setUpdatedAt(playlist.getUpdatedAt());

        // Set creator information if available
        if (playlist.getCreator() != null) {
            response.setCreatorName(playlist.getCreator().getUsername());
        }

        return response;
    }
}
