package com.musicshare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin dashboard statistics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    /**
     * Total number of users
     */
    private Long totalUsers;

    /**
     * Number of active users (enabled)
     */
    private Long activeUsers;

    /**
     * Total number of music tracks
     */
    private Long totalMusic;

    /**
     * Number of pending music awaiting approval
     */
    private Long pendingMusic;

    /**
     * Number of approved music
     */
    private Long approvedMusic;

    /**
     * Total number of playlists
     */
    private Long totalPlaylists;

    /**
     * Total number of comments
     */
    private Long totalComments;

    /**
     * Number of uploads today
     */
    private Long todayUploads;

    /**
     * Number of registrations today
     */
    private Long todayRegistrations;
}
