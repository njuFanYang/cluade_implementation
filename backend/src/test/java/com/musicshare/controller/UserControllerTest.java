package com.musicshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicshare.dto.request.ChangePasswordRequest;
import com.musicshare.dto.request.UpdateProfileRequest;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.security.CustomUserDetailsService;
import com.musicshare.security.JwtAuthenticationFilter;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse();
        mockUserResponse.setId(1L);
        mockUserResponse.setUsername("testuser");
        mockUserResponse.setEmail("test@example.com");
        mockUserResponse.setNickname("Test User");
        mockUserResponse.setEnabled(true);
        mockUserResponse.setRoles(Collections.emptySet());
    }

    @Test
    @DisplayName("GET /api/users/{id} - success returns user data")
    void getUserById_success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(mockUserResponse);
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - user not found returns error")
    void getUserById_notFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001));
    }

    @Test
    @DisplayName("GET /api/users/profile - authenticated user gets profile")
    @WithMockUser(username = "testuser")
    void getCurrentUserProfile_success() throws Exception {
        when(userService.getUserProfile("testuser")).thenReturn(mockUserResponse);
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("PUT /api/users/profile - success updates profile")
    @WithMockUser(username = "testuser")
    void updateProfile_success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("New Nick");
        UserResponse updated = new UserResponse();
        updated.setId(1L);
        updated.setUsername("testuser");
        updated.setNickname("New Nick");
        updated.setEnabled(true);
        updated.setRoles(Collections.emptySet());
        when(userService.updateProfile(eq("testuser"), any(UpdateProfileRequest.class))).thenReturn(updated);
        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("New Nick"));
    }

    @Test
    @DisplayName("PUT /api/users/password - success changes password")
    @WithMockUser(username = "testuser")
    void changePassword_success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("newPass123");
        doNothing().when(userService).changePassword(eq("testuser"), any(ChangePasswordRequest.class));
        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/users/password - wrong old password returns error code 1011")
    @WithMockUser(username = "testuser")
    void changePassword_wrongOldPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("newPass123");
        doThrow(new BusinessException(ErrorCode.OLD_PASSWORD_INCORRECT))
                .when(userService).changePassword(anyString(), any(ChangePasswordRequest.class));
        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1011));
    }
}
