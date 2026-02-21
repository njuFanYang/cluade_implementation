package com.musicshare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Follow statistics response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatsResponse {

    private Long userId;
    private Long followersCount;
    private Long followingCount;
}
