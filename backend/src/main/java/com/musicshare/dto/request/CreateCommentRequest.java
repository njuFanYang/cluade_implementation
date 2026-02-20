package com.musicshare.dto.request;

import com.musicshare.entity.Comment.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Comment Request DTO
 *
 * <p>Data transfer object for comment creation requests.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    /**
     * Comment content
     */
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment content must be between 1 and 1000 characters")
    private String content;

    /**
     * Target type (MUSIC, PLAYLIST)
     */
    @NotNull(message = "Target type is required")
    private TargetType targetType;

    /**
     * Target ID (music_id or playlist_id)
     */
    @NotNull(message = "Target ID is required")
    private Long targetId;

    /**
     * Parent comment ID (for replies)
     * Null for top-level comments
     */
    private Long parentCommentId;
}
