package com.musicshare.dto.response;

import com.musicshare.entity.Role;
import com.musicshare.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Response DTO
 *
 * <p>Data transfer object for user information in API responses.
 * Excludes sensitive information like password.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String bio;
    private String gender;
    private String phone;
    private String location;
    private Boolean enabled;
    private Set<String> roles;
    private Integer followersCount;
    private Integer followingCount;
    private Integer musicCount;
    private Integer playlistCount;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Create UserResponse from User entity
     *
     * @param user User entity
     * @return UserResponse instance
     */
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setGender(user.getGender());
        response.setPhone(user.getPhone());
        response.setLocation(user.getLocation());
        response.setEnabled(user.getEnabled());
        response.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        response.setFollowersCount(user.getFollowersCount());
        response.setFollowingCount(user.getFollowingCount());
        response.setMusicCount(user.getMusicCount());
        response.setPlaylistCount(user.getPlaylistCount());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

}
