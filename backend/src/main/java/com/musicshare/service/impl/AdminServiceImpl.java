package com.musicshare.service.impl;

import com.musicshare.dto.request.AssignRoleRequest;
import com.musicshare.dto.request.UpdateUserStatusRequest;
import com.musicshare.dto.response.AdminDashboardResponse;
import com.musicshare.dto.response.AdminUserResponse;
import com.musicshare.dto.response.MusicResponse;
import com.musicshare.entity.Music;
import com.musicshare.entity.MusicStatus;
import com.musicshare.entity.Role;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.*;
import com.musicshare.service.AdminService;
import com.musicshare.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Admin Service Implementation
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final RoleRepository roleRepository;
    private final PlaylistRepository playlistRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStatistics() {
        log.info("Fetching dashboard statistics by admin: {}", SecurityUtil.getCurrentUsername());

        // Get current date for today's statistics
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Calculate statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabled(true);
        long totalMusic = musicRepository.count();
        long pendingMusic = musicRepository.countByStatus(MusicStatus.PENDING);
        long approvedMusic = musicRepository.countByStatus(MusicStatus.APPROVED);
        long totalPlaylists = playlistRepository.count();
        long totalComments = commentRepository.count();
        long todayUploads = musicRepository.countByCreatedAtAfter(todayStart);
        long todayRegistrations = userRepository.countByCreatedAtAfter(todayStart);

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalMusic(totalMusic)
                .pendingMusic(pendingMusic)
                .approvedMusic(approvedMusic)
                .totalPlaylists(totalPlaylists)
                .totalComments(totalComments)
                .todayUploads(todayUploads)
                .todayRegistrations(todayRegistrations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(String keyword, Boolean enabled, Pageable pageable) {
        log.info("Fetching users with keyword: {}, enabled: {}, page: {}",
                keyword, enabled, pageable.getPageNumber());

        Page<User> users;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search by username or email
            users = userRepository.findByUsernameContainingOrEmailContaining(
                    keyword.trim(), keyword.trim(), pageable);
        } else if (enabled != null) {
            // Filter by enabled status
            users = userRepository.findByEnabled(enabled, pageable);
        } else {
            // Get all users
            users = userRepository.findAll(pageable);
        }

        return users.map(this::convertToAdminUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserDetails(Long userId) {
        log.info("Fetching user details for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return convertToAdminUserResponse(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        String adminUsername = SecurityUtil.getCurrentUsername();
        log.info("Admin {} updating status for userId: {}", adminUsername, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Prevent disabling admin users
        if (user.hasRole("ROLE_ADMIN") && Boolean.FALSE.equals(request.getEnabled())) {
            log.warn("Attempt to disable admin user {} by {}", user.getUsername(), adminUsername);
            throw new BusinessException(ErrorCode.CANNOT_DISABLE_ADMIN);
        }

        user.setEnabled(request.getEnabled());
        if (request.getLocked() != null) {
            user.setLocked(request.getLocked());
        }

        userRepository.save(user);
        log.info("User {} status updated: enabled={}, locked={}",
                user.getUsername(), user.getEnabled(), user.getLocked());
    }

    @Override
    @Transactional
    public void manageUserRole(AssignRoleRequest request) {
        String adminUsername = SecurityUtil.getCurrentUsername();
        log.info("Admin {} managing role for userId: {}, role: {}, action: {}",
                adminUsername, request.getUserId(), request.getRoleName(), request.getAction());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        if ("ADD".equals(request.getAction())) {
            user.addRole(role);
            log.info("Added role {} to user {}", request.getRoleName(), user.getUsername());
        } else if ("REMOVE".equals(request.getAction())) {
            // Prevent removing ROLE_ADMIN if this is the last admin
            if ("ROLE_ADMIN".equals(request.getRoleName())) {
                long adminCount = userRepository.findAll().stream()
                        .filter(u -> u.hasRole("ROLE_ADMIN"))
                        .count();

                if (adminCount <= 1) {
                    log.warn("Attempt to remove last admin role from {}", user.getUsername());
                    throw new BusinessException(ErrorCode.CANNOT_REMOVE_LAST_ADMIN);
                }
            }

            user.removeRole(role);
            log.info("Removed role {} from user {}", request.getRoleName(), user.getUsername());
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        String adminUsername = SecurityUtil.getCurrentUsername();
        log.info("Admin {} attempting to delete userId: {}", adminUsername, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Prevent deleting admin users
        if (user.hasRole("ROLE_ADMIN")) {
            log.warn("Attempt to delete admin user {} by {}", user.getUsername(), adminUsername);
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ADMIN);
        }

        userRepository.delete(user);
        log.info("User {} deleted by admin {}", user.getUsername(), adminUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getPendingMusic(Pageable pageable) {
        log.info("Fetching pending music, page: {}", pageable.getPageNumber());

        Page<Music> pendingMusic = musicRepository.findByStatus(MusicStatus.PENDING, pageable);
        return pendingMusic.map(this::convertToMusicResponse);
    }

    @Override
    @Transactional
    public void approveMusic(Long musicId) {
        String adminUsername = SecurityUtil.getCurrentUsername();
        log.info("Admin {} approving musicId: {}", adminUsername, musicId);

        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        music.setStatus(MusicStatus.APPROVED);
        musicRepository.save(music);

        log.info("Music {} approved by admin {}", music.getTitle(), adminUsername);
    }

    @Override
    @Transactional
    public void rejectMusic(Long musicId, String reason) {
        String adminUsername = SecurityUtil.getCurrentUsername();
        log.info("Admin {} rejecting musicId: {}, reason: {}", adminUsername, musicId, reason);

        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        music.setStatus(MusicStatus.REJECTED);
        // Note: If you want to store rejection reason, add a field to Music entity
        musicRepository.save(music);

        log.info("Music {} rejected by admin {}", music.getTitle(), adminUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getAllMusicByStatus(String status, Pageable pageable) {
        log.info("Fetching music by status: {}, page: {}", status, pageable.getPageNumber());

        MusicStatus musicStatus;
        try {
            musicStatus = MusicStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
        }

        Page<Music> music = musicRepository.findByStatus(musicStatus, pageable);
        return music.map(this::convertToMusicResponse);
    }

    /**
     * Convert User to AdminUserResponse
     */
    private AdminUserResponse convertToAdminUserResponse(User user) {
        long followersCount = followRepository.countByFollowingId(user.getId());
        long musicCount = musicRepository.countByUploaderId(user.getId());

        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .enabled(user.getEnabled())
                .locked(user.getLocked())
                .lastLoginIp(user.getLastLoginIp())
                .lastLoginTime(user.getLastLoginTime())
                .musicCount(musicCount)
                .followersCount(followersCount)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert Music to MusicResponse
     */
    private MusicResponse convertToMusicResponse(Music music) {
        return MusicResponse.builder()
                .id(music.getId())
                .title(music.getTitle())
                .artist(music.getArtist())
                .album(music.getAlbum() != null ? music.getAlbum().getName() : null)
                .genreId(music.getGenreId())
                .duration(music.getDuration())
                .filePath(music.getFilePath())
                .coverImage(music.getCoverImage())
                .isPublic(music.getIsPublic())
                .status(music.getStatus().name())
                .playCount(music.getPlayCount())
                .likeCount(music.getLikeCount())
                .uploaderId(music.getUploader() != null ? music.getUploader().getId() : null)
                .uploaderName(music.getUploader() != null ? music.getUploader().getUsername() : null)
                .createdAt(music.getCreatedAt())
                .updatedAt(music.getUpdatedAt())
                .build();
    }
}
