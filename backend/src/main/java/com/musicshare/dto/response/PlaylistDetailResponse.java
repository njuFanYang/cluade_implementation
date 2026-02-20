package com.musicshare.dto.response;

import com.musicshare.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Playlist Detail Response DTO
 *
 * <p>Extended playlist response with music list.
 * Used for detailed playlist views.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlaylistDetailResponse extends PlaylistResponse {

    private List<MusicResponse> musicList;

    /**
     * Create PlaylistDetailResponse from Playlist entity
     *
     * @param playlist  Playlist entity
     * @param musicList List of music in the playlist
     * @return PlaylistDetailResponse instance
     */
    public static PlaylistDetailResponse fromEntity(Playlist playlist, List<MusicResponse> musicList) {
        if (playlist == null) {
            return null;
        }

        PlaylistDetailResponse response = new PlaylistDetailResponse();

        // Copy basic fields from parent
        PlaylistResponse basicResponse = PlaylistResponse.fromEntity(playlist);
        response.setId(basicResponse.getId());
        response.setName(basicResponse.getName());
        response.setDescription(basicResponse.getDescription());
        response.setCoverImage(basicResponse.getCoverImage());
        response.setCreatorId(basicResponse.getCreatorId());
        response.setCreatorName(basicResponse.getCreatorName());
        response.setIsPublic(basicResponse.getIsPublic());
        response.setMusicCount(basicResponse.getMusicCount());
        response.setPlayCount(basicResponse.getPlayCount());
        response.setLikeCount(basicResponse.getLikeCount());
        response.setCreatedAt(basicResponse.getCreatedAt());
        response.setUpdatedAt(basicResponse.getUpdatedAt());

        // Set music list
        response.setMusicList(musicList);

        return response;
    }
}
