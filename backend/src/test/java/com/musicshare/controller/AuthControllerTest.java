package com.musicshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicshare.dto.request.LoginRequest;
import com.musicshare.dto.request.RegisterRequest;
import com.musicshare.dto.response.LoginResponse;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponse mockUserResponse;
    private LoginResponse mockLoginResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse();
        mockUserResponse.setId(1L);
        mockUserResponse.setUsername("testuser");
        mockUserResponse.setEmail("test@example.com");
        mockUserResponse.setNickname("Test User");
        mockUserResponse.setEnabled(true);
        mockUserResponse.setRoles(Collections.emptySet());

        mockLoginResponse = new LoginResponse("jwt-token-123", 86400000L, mockUserResponse);
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("POST /api/auth/register - success returns 201 with user data")
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setNickname("Test User");

        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUserResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @DisplayName("POST /api/auth/register - missing required fields returns 400")
    void register_missingFields() throws Exception {
        // Empty body
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - username already exists returns error")
    void register_usernameAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // GlobalExceptionHandler returns 200 with error code
                .andExpect(jsonPath("$.code").value(1008));
    }

    @Test
    @DisplayName("POST /api/auth/register - email already exists returns error")
    void register_emailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1007));
    }

    @Test
    @DisplayName("POST /api/auth/register - password mismatch returns error")
    void register_passwordMismatch() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("different");

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.PASSWORD_MISMATCH));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1006));
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("POST /api/auth/login - success returns 200 with token")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");
        request.setRememberMe(false);

        when(userService.login(any(LoginRequest.class))).thenReturn(mockLoginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.data.userInfo.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("POST /api/auth/login - invalid credentials returns error")
    void login_invalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpassword");

        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1003));
    }

    @Test
    @DisplayName("POST /api/auth/login - disabled account returns error")
    void login_disabledAccount() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("disabled");
        request.setPassword("password");

        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.ACCOUNT_DISABLED));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1010));
    }

    @Test
    @DisplayName("POST /api/auth/login - missing required fields returns 400")
    void login_missingFields() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGOUT TESTS ====================

    @Test
    @DisplayName("POST /api/auth/logout - returns 200 success")
    void logout_success() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}
