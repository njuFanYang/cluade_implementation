package com.musicshare.dto.response;

import com.musicshare.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Follow Response DTO
 *
 * <p>Data transfer object for follow relationship information in API responses.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {

    private Long id;
    private Long followerId;
    private String followerUsername;
    private String followerAvatar;
    private Long followingId;
    private String followingUsername;
    private String followingAvatar;
    private LocalDateTime createdAt;

    /**
     * Create FollowResponse from Follow entity
     *
     * @param follow Follow entity
     * @return FollowResponse instance
     */
    public static FollowResponse fromEntity(Follow follow) {
        if (follow == null) {
            return null;
        }

        FollowResponse response = new FollowResponse();
        response.setId(follow.getId());
        response.setFollowerId(follow.getFollowerId());
        response.setFollowingId(follow.getFollowingId());
        response.setCreatedAt(follow.getCreatedAt());

        // Set follower information if available
        if (follow.getFollower() != null) {
            response.setFollowerUsername(follow.getFollower().getUsername());
            response.setFollowerAvatar(follow.getFollower().getAvatar());
        }

        // Set following information if available
        if (follow.getFollowing() != null) {
            response.setFollowingUsername(follow.getFollowing().getUsername());
            response.setFollowingAvatar(follow.getFollowing().getAvatar());
        }

        return response;
    }
}
