package com.musicshare.dto.response;

import com.musicshare.entity.Music;
import com.musicshare.entity.MusicStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Music Response DTO
 *
 * <p>Data transfer object for music information in API responses.
 * Contains basic music information without sensitive details.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicResponse {

    private Long id;
    private String title;
    private String artist;
    private Long albumId;
    private String albumTitle;
    private Long genreId;
    private String genreName;
    private String genreNameZh;
    private Integer duration;
    private String fileFormat;
    private Long fileSize;
    private String coverImage;
    private LocalDate releaseDate;
    private Long uploaderId;
    private String uploaderName;
    private Integer playCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer downloadCount;
    private MusicStatus status;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Create MusicResponse from Music entity
     *
     * @param music Music entity
     * @return MusicResponse instance
     */
    public static MusicResponse fromEntity(Music music) {
        if (music == null) {
            return null;
        }

        MusicResponse response = new MusicResponse();
        response.setId(music.getId());
        response.setTitle(music.getTitle());
        response.setArtist(music.getArtist());
        response.setAlbumId(music.getAlbumId());
        response.setGenreId(music.getGenreId());
        response.setDuration(music.getDuration());
        response.setFileFormat(music.getFileFormat());
        response.setFileSize(music.getFileSize());
        response.setCoverImage(music.getCoverImage());
        response.setReleaseDate(music.getReleaseDate());
        response.setUploaderId(music.getUploaderId());
        response.setPlayCount(music.getPlayCount());
        response.setLikeCount(music.getLikeCount());
        response.setCommentCount(music.getCommentCount());
        response.setDownloadCount(music.getDownloadCount());
        response.setStatus(music.getStatus());
        response.setIsPublic(music.getIsPublic());
        response.setCreatedAt(music.getCreatedAt());
        response.setUpdatedAt(music.getUpdatedAt());

        // Set genre information if available
        if (music.getGenre() != null) {
            response.setGenreName(music.getGenre().getName());
            response.setGenreNameZh(music.getGenre().getNameZh());
        }

        // Set album information if available
        if (music.getAlbum() != null) {
            response.setAlbumTitle(music.getAlbum().getTitle());
        }

        // Set uploader information if available
        if (music.getUploader() != null) {
            response.setUploaderName(music.getUploader().getUsername());
        }

        return response;
    }
}
