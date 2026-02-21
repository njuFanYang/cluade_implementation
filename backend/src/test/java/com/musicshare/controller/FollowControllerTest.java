package com.musicshare.controller;

import com.musicshare.dto.response.FollowStatsResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.security.CustomUserDetailsService;
import com.musicshare.security.JwtAuthenticationFilter;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FollowController Integration Tests")
class FollowControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private FollowService followService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse();
        mockUserResponse.setId(2L);
        mockUserResponse.setUsername("otheruser");
        mockUserResponse.setEmail("other@example.com");
        mockUserResponse.setNickname("Other User");
        mockUserResponse.setEnabled(true);
        mockUserResponse.setRoles(Collections.emptySet());
    }

    // ==================== FOLLOW ====================

    @Test
    @DisplayName("POST /api/follows/{userId} - success returns 201")
    void follow_success() throws Exception {
        when(followService.follow(eq(2L))).thenReturn(null);

        mockMvc.perform(post("/api/follows/2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Followed successfully"));
    }

    @Test
    @DisplayName("POST /api/follows/{userId} - cannot follow self returns error 4006")
    void follow_cannotFollowSelf() throws Exception {
        doThrow(new BusinessException(ErrorCode.CANNOT_FOLLOW_SELF))
                .when(followService).follow(anyLong());

        mockMvc.perform(post("/api/follows/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4006));
    }

    @Test
    @DisplayName("POST /api/follows/{userId} - already following returns error 4004")
    void follow_alreadyFollowing() throws Exception {
        doThrow(new BusinessException(ErrorCode.ALREADY_FOLLOWING))
                .when(followService).follow(anyLong());

        mockMvc.perform(post("/api/follows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4004));
    }

    @Test
    @DisplayName("POST /api/follows/{userId} - user not found returns error")
    void follow_userNotFound() throws Exception {
        doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                .when(followService).follow(anyLong());

        mockMvc.perform(post("/api/follows/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001));
    }

    // ==================== UNFOLLOW ====================

    @Test
    @DisplayName("DELETE /api/follows/{userId} - success returns 200")
    void unfollow_success() throws Exception {
        doNothing().when(followService).unfollow(eq(2L));

        mockMvc.perform(delete("/api/follows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Unfollowed successfully"));
    }

    @Test
    @DisplayName("DELETE /api/follows/{userId} - not following returns error 4005")
    void unfollow_notFollowingYet() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOLLOWING_YET))
                .when(followService).unfollow(anyLong());

        mockMvc.perform(delete("/api/follows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4005));
    }

    // ==================== CHECK FOLLOWING STATUS ====================

    @Test
    @DisplayName("GET /api/follows/check/{userId} - returns true when following")
    void isFollowing_returnsTrue() throws Exception {
        when(followService.isFollowing(eq(2L))).thenReturn(true);

        mockMvc.perform(get("/api/follows/check/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("GET /api/follows/check/{userId} - returns false when not following")
    void isFollowing_returnsFalse() throws Exception {
        when(followService.isFollowing(eq(3L))).thenReturn(false);

        mockMvc.perform(get("/api/follows/check/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    // ==================== GET FOLLOWERS ====================

    @Test
    @DisplayName("GET /api/follows/{userId}/followers - returns 200 with follower list")
    void getFollowers_success() throws Exception {
        PageImpl<UserResponse> page = new PageImpl<>(List.of(mockUserResponse));
        when(followService.getFollowers(eq(2L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/follows/2/followers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].username").value("otheruser"));
    }

    @Test
    @DisplayName("GET /api/follows/{userId}/followers - no followers returns empty page")
    void getFollowers_empty() throws Exception {
        PageImpl<UserResponse> page = new PageImpl<>(Collections.emptyList());
        when(followService.getFollowers(eq(5L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/follows/5/followers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    // ==================== GET FOLLOWING ====================

    @Test
    @DisplayName("GET /api/follows/{userId}/following - returns 200 with following list")
    void getFollowing_success() throws Exception {
        PageImpl<UserResponse> page = new PageImpl<>(List.of(mockUserResponse));
        when(followService.getFollowing(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/follows/1/following"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].username").value("otheruser"));
    }

    @Test
    @DisplayName("GET /api/follows/{userId}/following - no following returns empty page")
    void getFollowing_empty() throws Exception {
        PageImpl<UserResponse> page = new PageImpl<>(Collections.emptyList());
        when(followService.getFollowing(eq(5L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/follows/5/following"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    // ==================== GET FOLLOW STATS ====================

    @Test
    @DisplayName("GET /api/follows/{userId}/stats - returns follower and following counts")
    void getFollowStats_success() throws Exception {
        FollowStatsResponse stats = new FollowStatsResponse(2L, 42L, 17L);
        when(followService.getFollowStats(eq(2L))).thenReturn(stats);

        mockMvc.perform(get("/api/follows/2/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.followersCount").value(42))
                .andExpect(jsonPath("$.data.followingCount").value(17));
    }

    @Test
    @DisplayName("GET /api/follows/{userId}/stats - new user has zero counts")
    void getFollowStats_newUser() throws Exception {
        FollowStatsResponse stats = new FollowStatsResponse(10L, 0L, 0L);
        when(followService.getFollowStats(eq(10L))).thenReturn(stats);

        mockMvc.perform(get("/api/follows/10/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followersCount").value(0))
                .andExpect(jsonPath("$.data.followingCount").value(0));
    }
}
