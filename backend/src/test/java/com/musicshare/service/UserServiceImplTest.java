package com.musicshare.service;

import com.musicshare.dto.request.ChangePasswordRequest;
import com.musicshare.dto.request.LoginRequest;
import com.musicshare.dto.request.RegisterRequest;
import com.musicshare.dto.request.UpdateProfileRequest;
import com.musicshare.dto.response.LoginResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.entity.Role;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.repository.RoleRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private UserServiceImpl userService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setNickname("Test User");
        testUser.setEnabled(true);
        testUser.setLocked(false);
        testUser.setRoles(new HashSet<>(Collections.singleton(userRole)));
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("register - success with all fields")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setNickname("New User");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPass");
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setNickname("New User");
        savedUser.setEnabled(true);
        savedUser.setRoles(new HashSet<>(Collections.singleton(userRole)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("newuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - uses username as nickname when nickname is null")
    void register_usesUsernameAsNickname_whenNicknameNull() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setNickname(null);

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setNickname("newuser");
        savedUser.setEnabled(true);
        savedUser.setRoles(new HashSet<>(Collections.singleton(userRole)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        assertThat(response.getNickname()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("register - throws PASSWORD_MISMATCH when passwords don't match")
    void register_throwsPasswordMismatch() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("different");

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Password mismatch");
    }

    @Test
    @DisplayName("register - throws USERNAME_ALREADY_EXISTS when username taken")
    void register_throwsUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("register - throws EMAIL_ALREADY_EXISTS when email taken")
    void register_throwsEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already exists");
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("login - success returns token and user info")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");
        request.setRememberMe(false);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.getExpirationTime()).thenReturn(86400000L);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        LoginResponse response = userService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUserInfo()).isNotNull();
    }

    @Test
    @DisplayName("login - rememberMe extends expiration to 30 days")
    void login_rememberMe_extendsExpiration() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");
        request.setRememberMe(true);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.getExpirationTime()).thenReturn(86400000L);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        LoginResponse response = userService.login(request);

        long thirtyDaysMs = 30L * 24 * 60 * 60 * 1000;
        assertThat(response.getExpiresIn()).isEqualTo(thirtyDaysMs);
    }

    @Test
    @DisplayName("login - throws INVALID_CREDENTIALS for bad password")
    void login_throwsInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    @DisplayName("login - throws ACCOUNT_DISABLED for disabled account")
    void login_throwsAccountDisabled() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        testUser.setEnabled(false);
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("disabled");
    }

    // ==================== GET PROFILE TESTS ====================

    @Test
    @DisplayName("getUserProfile - success")
    void getUserProfile_success() {
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserProfile("testuser");

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getUserProfile - throws USER_NOT_FOUND")
    void getUserProfile_throwsUserNotFound() {
        when(userRepository.findByUsernameWithRoles("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("unknown"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");
    }

    // ==================== UPDATE PROFILE TESTS ====================

    @Test
    @DisplayName("updateProfile - updates only provided non-null fields")
    void updateProfile_updatesNonNullFields() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("New Nick");
        request.setBio("My bio");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile("testuser", request);

        verify(userRepository).save(argThat(u ->
                "New Nick".equals(u.getNickname()) && "My bio".equals(u.getBio())));
    }

    @Test
    @DisplayName("updateProfile - skips null fields")
    void updateProfile_skipsNullFields() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("New Nick");
        // bio, avatar, gender, phone, location all null

        testUser.setBio("Existing bio");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile("testuser", request);

        verify(userRepository).save(argThat(u ->
                "New Nick".equals(u.getNickname()) && "Existing bio".equals(u.getBio())));
    }

    @Test
    @DisplayName("updateProfile - throws USER_NOT_FOUND")
    void updateProfile_throwsUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile("unknown", new UpdateProfileRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");
    }

    // ==================== CHANGE PASSWORD TESTS ====================

    @Test
    @DisplayName("changePassword - success")
    void changePassword_success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("newPass123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPass123", testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPass123")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertThatCode(() -> userService.changePassword("testuser", request)).doesNotThrowAnyException();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword - throws OLD_PASSWORD_INCORRECT")
    void changePassword_throwsOldPasswordIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongOld");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("newPass123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongOld", testUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("testuser", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Old password is incorrect");
    }

    @Test
    @DisplayName("changePassword - throws PASSWORD_MISMATCH when new passwords don't match")
    void changePassword_throwsPasswordMismatch() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("differentPass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", testUser.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userService.changePassword("testuser", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Password mismatch");
    }

    @Test
    @DisplayName("changePassword - throws error when new password same as old")
    void changePassword_throwsWhenSamePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("samePass");
        request.setNewPassword("samePass");
        request.setConfirmPassword("samePass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("samePass", testUser.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userService.changePassword("testuser", request))
                .isInstanceOf(BusinessException.class);
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    @DisplayName("getUserById - success")
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getUserById - throws USER_NOT_FOUND")
    void getUserById_throwsUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");
    }
}
