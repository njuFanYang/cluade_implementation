package com.musicshare.dto.response;

import com.musicshare.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Music Detail Response DTO
 *
 * <p>Extended music response with additional details like description,
 * lyrics, and file information. Used for detailed music views.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MusicDetailResponse extends MusicResponse {

    private String description;
    private String lyrics;
    private String fileName;
    private String fileUrl;

    /**
     * Create MusicDetailResponse from Music entity
     *
     * @param music   Music entity
     * @param fileUrl File URL for streaming
     * @return MusicDetailResponse instance
     */
    public static MusicDetailResponse fromEntity(Music music, String fileUrl) {
        if (music == null) {
            return null;
        }

        MusicDetailResponse response = new MusicDetailResponse();

        // Copy basic fields from parent
        MusicResponse basicResponse = MusicResponse.fromEntity(music);
        response.setId(basicResponse.getId());
        response.setTitle(basicResponse.getTitle());
        response.setArtist(basicResponse.getArtist());
        response.setAlbumId(basicResponse.getAlbumId());
        response.setAlbumTitle(basicResponse.getAlbumTitle());
        response.setGenreId(basicResponse.getGenreId());
        response.setGenreName(basicResponse.getGenreName());
        response.setGenreNameZh(basicResponse.getGenreNameZh());
        response.setDuration(basicResponse.getDuration());
        response.setFileFormat(basicResponse.getFileFormat());
        response.setFileSize(basicResponse.getFileSize());
        response.setCoverImage(basicResponse.getCoverImage());
        response.setReleaseDate(basicResponse.getReleaseDate());
        response.setUploaderId(basicResponse.getUploaderId());
        response.setUploaderName(basicResponse.getUploaderName());
        response.setPlayCount(basicResponse.getPlayCount());
        response.setLikeCount(basicResponse.getLikeCount());
        response.setCommentCount(basicResponse.getCommentCount());
        response.setDownloadCount(basicResponse.getDownloadCount());
        response.setStatus(basicResponse.getStatus());
        response.setIsPublic(basicResponse.getIsPublic());
        response.setCreatedAt(basicResponse.getCreatedAt());
        response.setUpdatedAt(basicResponse.getUpdatedAt());

        // Set extended fields
        response.setDescription(music.getDescription());
        response.setLyrics(music.getLyrics());
        response.setFileName(music.getFileName());
        response.setFileUrl(fileUrl);

        return response;
    }
}
