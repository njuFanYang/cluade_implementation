package com.musicshare.dto.response;

import com.musicshare.entity.Like;
import com.musicshare.entity.Like.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Like Response DTO
 *
 * <p>Data transfer object for like information in API responses.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private Long id;
    private Long userId;
    private String username;
    private TargetType targetType;
    private Long targetId;
    private LocalDateTime createdAt;

    /**
     * Create LikeResponse from Like entity
     *
     * @param like Like entity
     * @return LikeResponse instance
     */
    public static LikeResponse fromEntity(Like like) {
        if (like == null) {
            return null;
        }

        LikeResponse response = new LikeResponse();
        response.setId(like.getId());
        response.setUserId(like.getUserId());
        response.setTargetType(like.getTargetType());
        response.setTargetId(like.getTargetId());
        response.setCreatedAt(like.getCreatedAt());

        // Set user information if available
        if (like.getUser() != null) {
            response.setUsername(like.getUser().getUsername());
        }

        return response;
    }
}
