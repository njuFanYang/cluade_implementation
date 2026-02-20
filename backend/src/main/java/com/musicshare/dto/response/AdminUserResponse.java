package com.musicshare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Admin user response with extended information
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminUserResponse extends UserResponse {
    /**
     * Whether the user account is enabled
     */
    private Boolean enabled;

    /**
     * Whether the user account is locked
     */
    private Boolean locked;

    /**
     * Last login IP address
     */
    private String lastLoginIp;

    /**
     * Last login time
     */
    private LocalDateTime lastLoginTime;

    /**
     * Number of music uploaded by this user
     */
    private Long musicCount;

    /**
     * Number of followers
     */
    private Long followersCount;
}
