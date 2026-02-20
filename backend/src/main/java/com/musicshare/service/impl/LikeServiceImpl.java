package com.musicshare.service.impl;

import com.musicshare.entity.Comment;
import com.musicshare.entity.Like;
import com.musicshare.entity.Music;
import com.musicshare.entity.Playlist;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.*;
import com.musicshare.service.LikeService;
import com.musicshare.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Like service implementation
 *
 * @author MusicShare Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Like like(Like.TargetType targetType, Long targetId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("User {} liking {} {}", userId, targetType, targetId);

        // Check if already liked
        if (likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED);
        }

        // Validate target exists
        validateTargetExists(targetType, targetId);

        // Create like
        Like like = new Like(userId, targetType, targetId);
        like = likeRepository.save(like);

        // Update target like count
        updateTargetLikeCount(targetType, targetId, 1);

        log.info("Like created successfully");
        return like;
    }

    @Override
    @Transactional
    public void unlike(Like.TargetType targetType, Long targetId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("User {} unliking {} {}", userId, targetType, targetId);

        // Check if liked
        Like like = likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_LIKED_YET));

        // Delete like
        likeRepository.delete(like);

        // Update target like count
        updateTargetLikeCount(targetType, targetId, -1);

        log.info("Like removed successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Like.TargetType targetType, Long targetId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return false;
        return likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Like.TargetType targetType, Long targetId) {
        return likeRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Like> getUserLikes(Long userId, Like.TargetType targetType, Pageable pageable) {
        return likeRepository.findByUserIdAndTargetType(userId, targetType, pageable);
    }

    private void validateTargetExists(Like.TargetType targetType, Long targetId) {
        boolean exists = switch (targetType) {
            case MUSIC -> musicRepository.existsById(targetId);
            case PLAYLIST -> playlistRepository.existsById(targetId);
            case COMMENT -> commentRepository.existsById(targetId);
        };

        if (!exists) {
            throw new BusinessException(ErrorCode.TARGET_NOT_FOUND);
        }
    }

    private void updateTargetLikeCount(Like.TargetType targetType, Long targetId, int delta) {
        switch (targetType) {
            case MUSIC -> {
                Music music = musicRepository.findById(targetId).orElse(null);
                if (music != null) {
                    int newCount = Math.max(0, music.getLikeCount() + delta);
                    music.setLikeCount(newCount);
                    musicRepository.save(music);
                }
            }
            case PLAYLIST -> {
                Playlist playlist = playlistRepository.findById(targetId).orElse(null);
                if (playlist != null) {
                    int newCount = Math.max(0, playlist.getLikeCount() + delta);
                    playlist.setLikeCount(newCount);
                    playlistRepository.save(playlist);
                }
            }
            case COMMENT -> {
                Comment comment = commentRepository.findById(targetId).orElse(null);
                if (comment != null) {
                    comment.incrementLikeCount();
                    commentRepository.save(comment);
                }
            }
        }
    }
}
