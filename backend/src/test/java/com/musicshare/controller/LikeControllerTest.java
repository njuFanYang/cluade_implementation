package com.musicshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicshare.entity.Like;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.security.CustomUserDetailsService;
import com.musicshare.security.JwtAuthenticationFilter;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LikeController Integration Tests")
class LikeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private LikeService likeService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/likes - like music successfully")
    @WithMockUser(username = "testuser")
    void likeMusicSuccess() throws Exception {
        Like like = new Like(1L, Like.TargetType.MUSIC, 10L);
        when(likeService.like(Like.TargetType.MUSIC, 10L)).thenReturn(like);

        mockMvc.perform(post("/api/likes")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/likes - already liked returns error")
    @WithMockUser(username = "testuser")
    void likeAlreadyLiked() throws Exception {
        when(likeService.like(any(), any())).thenThrow(new BusinessException(ErrorCode.ALREADY_LIKED));

        mockMvc.perform(post("/api/likes")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4002));
    }

    @Test
    @DisplayName("DELETE /api/likes - unlike music successfully")
    @WithMockUser(username = "testuser")
    void unlikeMusicSuccess() throws Exception {
        doNothing().when(likeService).unlike(Like.TargetType.MUSIC, 10L);

        mockMvc.perform(delete("/api/likes")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/likes - not liked yet returns error")
    @WithMockUser(username = "testuser")
    void unlikeNotLikedYet() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_LIKED_YET))
                .when(likeService).unlike(any(), any());

        mockMvc.perform(delete("/api/likes")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4003));
    }

    @Test
    @DisplayName("GET /api/likes/check - check if liked returns boolean")
    @WithMockUser(username = "testuser")
    void isLiked() throws Exception {
        when(likeService.isLiked(Like.TargetType.MUSIC, 10L)).thenReturn(true);

        mockMvc.perform(get("/api/likes/check")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("GET /api/likes/count - returns like count")
    void getLikeCount() throws Exception {
        when(likeService.getLikeCount(Like.TargetType.MUSIC, 10L)).thenReturn(42L);

        mockMvc.perform(get("/api/likes/count")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(42));
    }
}
