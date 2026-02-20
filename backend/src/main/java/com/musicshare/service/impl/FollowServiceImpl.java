package com.musicshare.service.impl;

import com.musicshare.entity.Follow;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.FollowRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.service.FollowService;
import com.musicshare.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Follow service implementation
 *
 * @author MusicShare Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Follow follow(Long followingId) {
        Long followerId = SecurityUtil.getCurrentUserId();
        log.info("User {} following user {}", followerId, followingId);

        // Cannot follow self
        if (followerId.equals(followingId)) {
            throw new BusinessException(ErrorCode.CANNOT_FOLLOW_SELF);
        }

        // Check if already following
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException(ErrorCode.ALREADY_FOLLOWING);
        }

        // Validate following user exists
        if (!userRepository.existsById(followingId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // Create follow
        Follow follow = new Follow(followerId, followingId);
        follow = followRepository.save(follow);

        // Update follower/following counts
        updateFollowCounts(followerId, followingId, 1);

        log.info("Follow created successfully");
        return follow;
    }

    @Override
    @Transactional
    public void unfollow(Long followingId) {
        Long followerId = SecurityUtil.getCurrentUserId();
        log.info("User {} unfollowing user {}", followerId, followingId);

        // Check if following
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOLLOWING_YET));

        // Delete follow
        followRepository.delete(follow);

        // Update follower/following counts
        updateFollowCounts(followerId, followingId, -1);

        log.info("Follow removed successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followingId) {
        Long followerId = SecurityUtil.getCurrentUserId();
        if (followerId == null) return false;
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Follow> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findFollowersByFollowingId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Follow> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findFollowingByFollowerId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    private void updateFollowCounts(Long followerId, Long followingId, int delta) {
        // Update follower's following count
        userRepository.findById(followerId).ifPresent(user -> {
            int newCount = Math.max(0, user.getFollowingCount() + delta);
            user.setFollowingCount(newCount);
            userRepository.save(user);
        });

        // Update following's follower count
        userRepository.findById(followingId).ifPresent(user -> {
            int newCount = Math.max(0, user.getFollowerCount() + delta);
            user.setFollowerCount(newCount);
            userRepository.save(user);
        });
    }
}
