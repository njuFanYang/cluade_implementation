package com.musicshare.service;

import com.musicshare.dto.response.FollowStatsResponse;
import com.musicshare.entity.Follow;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.repository.FollowRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.service.impl.FollowServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService Unit Tests")
class FollowServiceImplTest {

    @Mock private FollowRepository followRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private FollowServiceImpl followService;

    private static final Long FOLLOWER_ID = 1L;
    private static final Long FOLLOWING_ID = 2L;

    @BeforeEach
    void setUp() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        var principal = new org.springframework.security.core.userdetails.User(FOLLOWER_ID.toString(), "", authorities);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== FOLLOW TESTS ====================

    @Test
    @DisplayName("follow - success creates follow and updates counts")
    void follow_success() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(false);
        when(userRepository.existsById(FOLLOWING_ID)).thenReturn(true);
        Follow savedFollow = new Follow(FOLLOWER_ID, FOLLOWING_ID);
        when(followRepository.save(any(Follow.class))).thenReturn(savedFollow);

        User follower = new User();
        follower.setId(FOLLOWER_ID);
        follower.setFollowingCount(3);
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.save(follower)).thenReturn(follower);

        User followingUser = new User();
        followingUser.setId(FOLLOWING_ID);
        followingUser.setFollowersCount(5);
        when(userRepository.findById(FOLLOWING_ID)).thenReturn(Optional.of(followingUser));
        when(userRepository.save(followingUser)).thenReturn(followingUser);

        Follow result = followService.follow(FOLLOWING_ID);

        assertThat(result).isNotNull();
        verify(followRepository).save(any(Follow.class));
        verify(userRepository).save(argThat(u -> u.getId().equals(FOLLOWER_ID) && u.getFollowingCount() == 4));
        verify(userRepository).save(argThat(u -> u.getId().equals(FOLLOWING_ID) && u.getFollowersCount() == 6));
    }

    @Test
    @DisplayName("follow - throws CANNOT_FOLLOW_SELF")
    void follow_throwsCannotFollowSelf() {
        assertThatThrownBy(() -> followService.follow(FOLLOWER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot follow yourself");
    }

    @Test
    @DisplayName("follow - throws ALREADY_FOLLOWING when already followed")
    void follow_throwsAlreadyFollowing() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(true);

        assertThatThrownBy(() -> followService.follow(FOLLOWING_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Already following");
    }

    @Test
    @DisplayName("follow - throws USER_NOT_FOUND when target user doesn't exist")
    void follow_throwsUserNotFound() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, 99L)).thenReturn(false);
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> followService.follow(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");
    }

    // ==================== UNFOLLOW TESTS ====================

    @Test
    @DisplayName("unfollow - success removes follow and decrements counts")
    void unfollow_success() {
        Follow existingFollow = new Follow(FOLLOWER_ID, FOLLOWING_ID);
        when(followRepository.findByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID))
                .thenReturn(Optional.of(existingFollow));

        User follower = new User();
        follower.setId(FOLLOWER_ID);
        follower.setFollowingCount(4);
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.save(follower)).thenReturn(follower);

        User followingUser = new User();
        followingUser.setId(FOLLOWING_ID);
        followingUser.setFollowersCount(6);
        when(userRepository.findById(FOLLOWING_ID)).thenReturn(Optional.of(followingUser));
        when(userRepository.save(followingUser)).thenReturn(followingUser);

        assertThatCode(() -> followService.unfollow(FOLLOWING_ID)).doesNotThrowAnyException();
        verify(followRepository).delete(existingFollow);
        verify(userRepository).save(argThat(u -> u.getId().equals(FOLLOWER_ID) && u.getFollowingCount() == 3));
        verify(userRepository).save(argThat(u -> u.getId().equals(FOLLOWING_ID) && u.getFollowersCount() == 5));
    }

    @Test
    @DisplayName("unfollow - count does not go below 0")
    void unfollow_countNotBelowZero() {
        Follow existingFollow = new Follow(FOLLOWER_ID, FOLLOWING_ID);
        when(followRepository.findByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID))
                .thenReturn(Optional.of(existingFollow));

        User follower = new User();
        follower.setId(FOLLOWER_ID);
        follower.setFollowingCount(0);
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.save(follower)).thenReturn(follower);

        User followingUser = new User();
        followingUser.setId(FOLLOWING_ID);
        followingUser.setFollowersCount(0);
        when(userRepository.findById(FOLLOWING_ID)).thenReturn(Optional.of(followingUser));
        when(userRepository.save(followingUser)).thenReturn(followingUser);

        assertThatCode(() -> followService.unfollow(FOLLOWING_ID)).doesNotThrowAnyException();
        verify(userRepository).save(argThat(u -> u.getId().equals(FOLLOWER_ID) && u.getFollowingCount() == 0));
        verify(userRepository).save(argThat(u -> u.getId().equals(FOLLOWING_ID) && u.getFollowersCount() == 0));
    }

    @Test
    @DisplayName("unfollow - throws NOT_FOLLOWING_YET")
    void unfollow_throwsNotFollowingYet() {
        when(followRepository.findByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.unfollow(FOLLOWING_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Not following");
    }

    // ==================== IS FOLLOWING TESTS ====================

    @Test
    @DisplayName("isFollowing - returns true when following")
    void isFollowing_returnsTrue() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(true);

        assertThat(followService.isFollowing(FOLLOWING_ID)).isTrue();
    }

    @Test
    @DisplayName("isFollowing - returns false when not following")
    void isFollowing_returnsFalse() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(false);

        assertThat(followService.isFollowing(FOLLOWING_ID)).isFalse();
    }

    // ==================== GET STATS TESTS ====================

    @Test
    @DisplayName("getFollowStats - returns correct follower and following counts")
    void getFollowStats_returnsCorrectCounts() {
        when(followRepository.countByFollowingId(FOLLOWING_ID)).thenReturn(10L);
        when(followRepository.countByFollowerId(FOLLOWING_ID)).thenReturn(5L);

        FollowStatsResponse stats = followService.getFollowStats(FOLLOWING_ID);

        assertThat(stats.getUserId()).isEqualTo(FOLLOWING_ID);
        assertThat(stats.getFollowersCount()).isEqualTo(10L);
        assertThat(stats.getFollowingCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("getFollowStats - returns zeros when no follows")
    void getFollowStats_returnsZeros() {
        when(followRepository.countByFollowingId(99L)).thenReturn(0L);
        when(followRepository.countByFollowerId(99L)).thenReturn(0L);

        FollowStatsResponse stats = followService.getFollowStats(99L);

        assertThat(stats.getFollowersCount()).isEqualTo(0L);
        assertThat(stats.getFollowingCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("getFollowerCount - delegates to repository")
    void getFollowerCount_returnsCount() {
        when(followRepository.countByFollowingId(FOLLOWING_ID)).thenReturn(8L);

        assertThat(followService.getFollowerCount(FOLLOWING_ID)).isEqualTo(8L);
    }

    @Test
    @DisplayName("getFollowingCount - delegates to repository")
    void getFollowingCount_returnsCount() {
        when(followRepository.countByFollowerId(FOLLOWER_ID)).thenReturn(3L);

        assertThat(followService.getFollowingCount(FOLLOWER_ID)).isEqualTo(3L);
    }
}
