package com.musicshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicshare.dto.request.CreatePlaylistRequest;
import com.musicshare.dto.request.UpdatePlaylistRequest;
import com.musicshare.dto.response.PlaylistDetailResponse;
import com.musicshare.dto.response.PlaylistResponse;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.security.CustomUserDetailsService;
import com.musicshare.security.JwtAuthenticationFilter;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.PlaylistService;
import com.musicshare.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaylistController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PlaylistController Integration Tests")
class PlaylistControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PlaylistService playlistService;
    @MockBean private UserService userService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponse mockUserResponse;
    private PlaylistResponse mockPlaylistResponse;
    private PlaylistDetailResponse mockPlaylistDetailResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse();
        mockUserResponse.setId(1L);
        mockUserResponse.setUsername("testuser");
        mockUserResponse.setEmail("test@example.com");
        mockUserResponse.setNickname("Test User");
        mockUserResponse.setEnabled(true);
        mockUserResponse.setRoles(Collections.emptySet());

        mockPlaylistResponse = new PlaylistResponse();
        mockPlaylistResponse.setId(1L);
        mockPlaylistResponse.setName("Test Playlist");
        mockPlaylistResponse.setDescription("A test playlist");
        mockPlaylistResponse.setCreatorId(1L);
        mockPlaylistResponse.setCreatorName("testuser");
        mockPlaylistResponse.setIsPublic(true);

        mockPlaylistDetailResponse = new PlaylistDetailResponse();
        mockPlaylistDetailResponse.setId(1L);
        mockPlaylistDetailResponse.setName("Test Playlist");
        mockPlaylistDetailResponse.setCreatorId(1L);
        mockPlaylistDetailResponse.setCreatorName("testuser");
        mockPlaylistDetailResponse.setIsPublic(true);
        mockPlaylistDetailResponse.setMusicList(Collections.emptyList());
    }

    // ==================== CREATE PLAYLIST ====================

    @Test
    @DisplayName("POST /api/playlists - success returns 201 with playlist data")
    void createPlaylist_success() throws Exception {
        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("My New Playlist");
        request.setDescription("A nice playlist");
        request.setIsPublic(true);

        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        when(playlistService.createPlaylist(any(CreatePlaylistRequest.class), anyLong()))
                .thenReturn(mockPlaylistResponse);

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Test Playlist"))
                .andExpect(jsonPath("$.message").value("Playlist created successfully"));
    }

    @Test
    @DisplayName("POST /api/playlists - missing name returns 400")
    void createPlaylist_missingName() throws Exception {
        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/playlists - duplicate name returns error code 3002")
    void createPlaylist_nameAlreadyExists() throws Exception {
        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("Existing Playlist");

        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        when(playlistService.createPlaylist(any(), anyLong()))
                .thenThrow(new BusinessException(ErrorCode.PLAYLIST_NAME_EXISTS));

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3002));
    }

    // ==================== GET PLAYLIST DETAIL ====================

    @Test
    @DisplayName("GET /api/playlists/{id} - success returns 200 with playlist details")
    void getPlaylistDetail_success() throws Exception {
        when(playlistService.getPlaylistDetail(eq(1L), any()))
                .thenReturn(mockPlaylistDetailResponse);

        mockMvc.perform(get("/api/playlists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Playlist"));
    }

    @Test
    @DisplayName("GET /api/playlists/{id} - not found returns error code 3001")
    void getPlaylistDetail_notFound() throws Exception {
        when(playlistService.getPlaylistDetail(eq(999L), any()))
                .thenThrow(new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        mockMvc.perform(get("/api/playlists/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3001));
    }

    @Test
    @DisplayName("GET /api/playlists/{id} - private playlist without access returns error 3006")
    void getPlaylistDetail_privatePlaylist() throws Exception {
        when(playlistService.getPlaylistDetail(eq(5L), any()))
                .thenThrow(new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED));

        mockMvc.perform(get("/api/playlists/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3006));
    }

    // ==================== UPDATE PLAYLIST ====================

    @Test
    @DisplayName("PUT /api/playlists/{id} - success returns 200 with updated playlist")
    void updatePlaylist_success() throws Exception {
        UpdatePlaylistRequest request = new UpdatePlaylistRequest();
        request.setName("Updated Playlist Name");

        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        when(playlistService.updatePlaylist(eq(1L), any(UpdatePlaylistRequest.class), anyLong()))
                .thenReturn(mockPlaylistResponse);

        mockMvc.perform(put("/api/playlists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Playlist updated successfully"));
    }

    @Test
    @DisplayName("PUT /api/playlists/{id} - non-owner returns error 3006")
    void updatePlaylist_permissionDenied() throws Exception {
        UpdatePlaylistRequest request = new UpdatePlaylistRequest();
        request.setName("Stolen Playlist");

        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        when(playlistService.updatePlaylist(anyLong(), any(), anyLong()))
                .thenThrow(new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED));

        mockMvc.perform(put("/api/playlists/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3006));
    }

    // ==================== DELETE PLAYLIST ====================

    @Test
    @DisplayName("DELETE /api/playlists/{id} - success returns 200")
    void deletePlaylist_success() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        doNothing().when(playlistService).deletePlaylist(eq(1L), anyLong());

        mockMvc.perform(delete("/api/playlists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Playlist deleted successfully"));
    }

    @Test
    @DisplayName("DELETE /api/playlists/{id} - non-owner returns error 3006")
    void deletePlaylist_permissionDenied() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        doThrow(new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED))
                .when(playlistService).deletePlaylist(anyLong(), anyLong());

        mockMvc.perform(delete("/api/playlists/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3006));
    }

    // ==================== PUBLIC PLAYLISTS ====================

    @Test
    @DisplayName("GET /api/playlists/public - returns 200 with page of playlists")
    void getPublicPlaylists_success() throws Exception {
        PageImpl<PlaylistResponse> page = new PageImpl<>(List.of(mockPlaylistResponse));
        when(playlistService.getPublicPlaylists(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/playlists/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Playlist"));
    }

    @Test
    @DisplayName("GET /api/playlists/public - empty result returns 200 with empty page")
    void getPublicPlaylists_empty() throws Exception {
        PageImpl<PlaylistResponse> page = new PageImpl<>(Collections.emptyList());
        when(playlistService.getPublicPlaylists(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/playlists/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    // ==================== SEARCH PLAYLISTS ====================

    @Test
    @DisplayName("GET /api/playlists/search - returns matching playlists")
    void searchPlaylists_success() throws Exception {
        PageImpl<PlaylistResponse> page = new PageImpl<>(List.of(mockPlaylistResponse));
        when(playlistService.searchPlaylists(eq("test"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/playlists/search").param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Playlist"));
    }

    // ==================== POPULAR PLAYLISTS ====================

    @Test
    @DisplayName("GET /api/playlists/popular - returns popular playlists ordered by play count")
    void getPopularPlaylists_success() throws Exception {
        PageImpl<PlaylistResponse> page = new PageImpl<>(List.of(mockPlaylistResponse));
        when(playlistService.getPopularPlaylists(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/playlists/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Playlist"));
    }

    // ==================== ADD / REMOVE MUSIC ====================

    @Test
    @DisplayName("POST /api/playlists/{id}/music/{musicId} - success returns 200")
    void addMusicToPlaylist_success() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        doNothing().when(playlistService).addMusicToPlaylist(eq(1L), eq(10L), anyLong());

        mockMvc.perform(post("/api/playlists/1/music/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Music added to playlist"));
    }

    @Test
    @DisplayName("POST /api/playlists/{id}/music/{musicId} - already in playlist returns error 3003")
    void addMusicToPlaylist_alreadyInPlaylist() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        doThrow(new BusinessException(ErrorCode.MUSIC_ALREADY_IN_PLAYLIST))
                .when(playlistService).addMusicToPlaylist(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/api/playlists/1/music/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3003));
    }

    @Test
    @DisplayName("DELETE /api/playlists/{id}/music/{musicId} - success returns 200")
    void removeMusicFromPlaylist_success() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        doNothing().when(playlistService).removeMusicFromPlaylist(eq(1L), eq(10L), anyLong());

        mockMvc.perform(delete("/api/playlists/1/music/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Music removed from playlist"));
    }

    @Test
    @DisplayName("DELETE /api/playlists/{id}/music/{musicId} - music not in playlist returns error 3004")
    void removeMusicFromPlaylist_notInPlaylist() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        doThrow(new BusinessException(ErrorCode.MUSIC_NOT_IN_PLAYLIST))
                .when(playlistService).removeMusicFromPlaylist(anyLong(), anyLong(), anyLong());

        mockMvc.perform(delete("/api/playlists/1/music/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3004));
    }

    // ==================== MY PLAYLISTS ====================

    @Test
    @DisplayName("GET /api/playlists/my - returns current user playlists")
    void getMyPlaylists_success() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        PageImpl<PlaylistResponse> page = new PageImpl<>(List.of(mockPlaylistResponse));
        when(playlistService.getPlaylistsByCreator(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/playlists/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Playlist"));
    }
}
