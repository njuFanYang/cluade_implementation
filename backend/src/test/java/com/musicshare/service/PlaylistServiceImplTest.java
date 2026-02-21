package com.musicshare.service;

import com.musicshare.dto.request.CreatePlaylistRequest;
import com.musicshare.dto.request.UpdatePlaylistRequest;
import com.musicshare.dto.response.PlaylistDetailResponse;
import com.musicshare.dto.response.PlaylistResponse;
import com.musicshare.entity.Music;
import com.musicshare.entity.Playlist;
import com.musicshare.entity.PlaylistMusic;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.repository.MusicRepository;
import com.musicshare.repository.PlaylistMusicRepository;
import com.musicshare.repository.PlaylistRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.service.impl.PlaylistServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaylistService Unit Tests")
class PlaylistServiceImplTest {

    @Mock private PlaylistRepository playlistRepository;
    @Mock private PlaylistMusicRepository playlistMusicRepository;
    @Mock private MusicRepository musicRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private PlaylistServiceImpl playlistService;

    private static final Long USER_ID = 1L;
    private static final Long PLAYLIST_ID = 10L;
    private static final Long MUSIC_ID = 20L;

    private User testUser;
    private Playlist testPlaylist;
    private Music testMusic;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername("testuser");
        testUser.setPlaylistCount(2);

        testPlaylist = new Playlist();
        testPlaylist.setId(PLAYLIST_ID);
        testPlaylist.setName("My Playlist");
        testPlaylist.setCreatorId(USER_ID);
        testPlaylist.setIsPublic(true);
        testPlaylist.setMusicCount(3);
        testPlaylist.setLikeCount(0);

        testMusic = new Music();
        testMusic.setId(MUSIC_ID);
        testMusic.setTitle("Test Song");
        testMusic.setArtist("Test Artist");
        testMusic.setDuration(180);
        testMusic.setFilePath("/uploads/test.mp3");
        testMusic.setFileName("test.mp3");
        testMusic.setFileSize(1024L);
        testMusic.setFileFormat("mp3");
    }

    // ==================== CREATE PLAYLIST TESTS ====================

    @Test
    @DisplayName("createPlaylist - success")
    void createPlaylist_success() {
        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("New Playlist");
        request.setDescription("A test playlist");
        request.setIsPublic(true);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(playlistRepository.findByIdWithCreator(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        PlaylistResponse response = playlistService.createPlaylist(request, USER_ID);

        assertThat(response).isNotNull();
        verify(playlistRepository).save(any(Playlist.class));
        verify(userRepository).save(argThat(u -> u.getPlaylistCount() == 3));
    }

    @Test
    @DisplayName("createPlaylist - defaults isPublic to true when null")
    void createPlaylist_defaultsIsPublicTrue() {
        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("New Playlist");
        request.setIsPublic(null);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(playlistRepository.findByIdWithCreator(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        playlistService.createPlaylist(request, USER_ID);

        verify(playlistRepository).save(argThat(p -> Boolean.TRUE.equals(p.getIsPublic())));
    }

    @Test
    @DisplayName("createPlaylist - throws USER_NOT_FOUND when creator doesn't exist")
    void createPlaylist_throwsUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.createPlaylist(new CreatePlaylistRequest(), 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");
    }

    // ==================== GET PLAYLIST DETAIL TESTS ====================

    @Test
    @DisplayName("getPlaylistDetail - returns public playlist to anyone")
    void getPlaylistDetail_publicPlaylist() {
        when(playlistRepository.findByIdWithCreator(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(playlistMusicRepository.findByPlaylistIdWithMusicAndGenre(PLAYLIST_ID)).thenReturn(Collections.emptyList());

        PlaylistDetailResponse response = playlistService.getPlaylistDetail(PLAYLIST_ID, USER_ID);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("getPlaylistDetail - returns private playlist to owner")
    void getPlaylistDetail_privatePlaylist_ownerAccess() {
        testPlaylist.setIsPublic(false);
        when(playlistRepository.findByIdWithCreator(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(playlistMusicRepository.findByPlaylistIdWithMusicAndGenre(PLAYLIST_ID)).thenReturn(Collections.emptyList());

        PlaylistDetailResponse response = playlistService.getPlaylistDetail(PLAYLIST_ID, USER_ID);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("getPlaylistDetail - throws PLAYLIST_PERMISSION_DENIED for private playlist when not owner")
    void getPlaylistDetail_privatePlaylist_throwsPermissionDenied() {
        testPlaylist.setIsPublic(false);
        when(playlistRepository.findByIdWithCreator(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThatThrownBy(() -> playlistService.getPlaylistDetail(PLAYLIST_ID, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    @DisplayName("getPlaylistDetail - throws PLAYLIST_NOT_FOUND")
    void getPlaylistDetail_throwsNotFound() {
        when(playlistRepository.findByIdWithCreator(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.getPlaylistDetail(999L, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Playlist not found");
    }

    // ==================== UPDATE PLAYLIST TESTS ====================

    @Test
    @DisplayName("updatePlaylist - owner can update")
    void updatePlaylist_ownerCanUpdate() {
        UpdatePlaylistRequest request = new UpdatePlaylistRequest();
        request.setName("Updated Name");
        request.setDescription("Updated desc");

        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);
        when(playlistRepository.findByIdWithCreator(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        PlaylistResponse response = playlistService.updatePlaylist(PLAYLIST_ID, request, USER_ID);

        assertThat(response).isNotNull();
        verify(playlistRepository).save(argThat(p -> "Updated Name".equals(p.getName())));
    }

    @Test
    @DisplayName("updatePlaylist - throws PLAYLIST_PERMISSION_DENIED for non-owner")
    void updatePlaylist_throwsPermissionDenied() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThatThrownBy(() -> playlistService.updatePlaylist(PLAYLIST_ID, new UpdatePlaylistRequest(), 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    @DisplayName("updatePlaylist - throws PLAYLIST_NOT_FOUND")
    void updatePlaylist_throwsNotFound() {
        when(playlistRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.updatePlaylist(999L, new UpdatePlaylistRequest(), USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Playlist not found");
    }

    // ==================== DELETE PLAYLIST TESTS ====================

    @Test
    @DisplayName("deletePlaylist - owner can delete and decrements user playlist count")
    void deletePlaylist_ownerCanDelete() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertThatCode(() -> playlistService.deletePlaylist(PLAYLIST_ID, USER_ID)).doesNotThrowAnyException();
        verify(playlistMusicRepository).deleteByPlaylistId(PLAYLIST_ID);
        verify(playlistRepository).delete(testPlaylist);
        verify(userRepository).save(argThat(u -> u.getPlaylistCount() == 1));
    }

    @Test
    @DisplayName("deletePlaylist - throws PLAYLIST_PERMISSION_DENIED for non-owner")
    void deletePlaylist_throwsPermissionDenied() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThatThrownBy(() -> playlistService.deletePlaylist(PLAYLIST_ID, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Permission denied");
    }

    // ==================== ADD MUSIC TO PLAYLIST TESTS ====================

    @Test
    @DisplayName("addMusicToPlaylist - success adds at next position")
    void addMusicToPlaylist_success() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(testMusic));
        when(playlistMusicRepository.existsByPlaylistIdAndMusicId(PLAYLIST_ID, MUSIC_ID)).thenReturn(false);
        when(playlistMusicRepository.getMaxPosition(PLAYLIST_ID)).thenReturn(2);
        when(playlistMusicRepository.save(any(PlaylistMusic.class))).thenReturn(new PlaylistMusic());
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        assertThatCode(() -> playlistService.addMusicToPlaylist(PLAYLIST_ID, MUSIC_ID, USER_ID)).doesNotThrowAnyException();
        verify(playlistMusicRepository).save(argThat(pm -> pm.getPosition() == 3));
        verify(playlistRepository).save(argThat(p -> p.getMusicCount() == 4));
    }

    @Test
    @DisplayName("addMusicToPlaylist - position 0 when first music")
    void addMusicToPlaylist_firstMusicPosition0() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(testMusic));
        when(playlistMusicRepository.existsByPlaylistIdAndMusicId(PLAYLIST_ID, MUSIC_ID)).thenReturn(false);
        when(playlistMusicRepository.getMaxPosition(PLAYLIST_ID)).thenReturn(null);
        when(playlistMusicRepository.save(any(PlaylistMusic.class))).thenReturn(new PlaylistMusic());
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        assertThatCode(() -> playlistService.addMusicToPlaylist(PLAYLIST_ID, MUSIC_ID, USER_ID)).doesNotThrowAnyException();
        verify(playlistMusicRepository).save(argThat(pm -> pm.getPosition() == 0));
    }

    @Test
    @DisplayName("addMusicToPlaylist - throws MUSIC_ALREADY_IN_PLAYLIST")
    void addMusicToPlaylist_throwsAlreadyInPlaylist() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(testMusic));
        when(playlistMusicRepository.existsByPlaylistIdAndMusicId(PLAYLIST_ID, MUSIC_ID)).thenReturn(true);

        assertThatThrownBy(() -> playlistService.addMusicToPlaylist(PLAYLIST_ID, MUSIC_ID, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Music already in playlist");
    }

    @Test
    @DisplayName("addMusicToPlaylist - throws PLAYLIST_PERMISSION_DENIED for non-owner")
    void addMusicToPlaylist_throwsPermissionDenied() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThatThrownBy(() -> playlistService.addMusicToPlaylist(PLAYLIST_ID, MUSIC_ID, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    @DisplayName("addMusicToPlaylist - throws MUSIC_NOT_FOUND when music doesn't exist")
    void addMusicToPlaylist_throwsMusicNotFound() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(musicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.addMusicToPlaylist(PLAYLIST_ID, 99L, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Music not found");
    }

    // ==================== REMOVE MUSIC FROM PLAYLIST TESTS ====================

    @Test
    @DisplayName("removeMusicFromPlaylist - success reorders remaining music")
    void removeMusicFromPlaylist_success() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(playlistMusicRepository.existsByPlaylistIdAndMusicId(PLAYLIST_ID, MUSIC_ID)).thenReturn(true);
        when(playlistMusicRepository.findByPlaylistIdOrderByPositionAsc(PLAYLIST_ID))
                .thenReturn(Collections.emptyList());
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        assertThatCode(() -> playlistService.removeMusicFromPlaylist(PLAYLIST_ID, MUSIC_ID, USER_ID)).doesNotThrowAnyException();
        verify(playlistMusicRepository).deleteByPlaylistIdAndMusicId(PLAYLIST_ID, MUSIC_ID);
        verify(playlistRepository).save(argThat(p -> p.getMusicCount() == 2));
    }

    @Test
    @DisplayName("removeMusicFromPlaylist - throws MUSIC_NOT_IN_PLAYLIST")
    void removeMusicFromPlaylist_throwsMusicNotInPlaylist() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));
        when(playlistMusicRepository.existsByPlaylistIdAndMusicId(PLAYLIST_ID, MUSIC_ID)).thenReturn(false);

        assertThatThrownBy(() -> playlistService.removeMusicFromPlaylist(PLAYLIST_ID, MUSIC_ID, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Music not in playlist");
    }

    // ==================== QUERY TESTS ====================

    @Test
    @DisplayName("canUserAccessPlaylist - returns true for public playlist")
    void canUserAccessPlaylist_trueForPublic() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThat(playlistService.canUserAccessPlaylist(PLAYLIST_ID, 99L)).isTrue();
    }

    @Test
    @DisplayName("canUserAccessPlaylist - returns true for owner of private playlist")
    void canUserAccessPlaylist_trueForOwner() {
        testPlaylist.setIsPublic(false);
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThat(playlistService.canUserAccessPlaylist(PLAYLIST_ID, USER_ID)).isTrue();
    }

    @Test
    @DisplayName("canUserAccessPlaylist - returns false for non-owner of private playlist")
    void canUserAccessPlaylist_falseForNonOwner() {
        testPlaylist.setIsPublic(false);
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThat(playlistService.canUserAccessPlaylist(PLAYLIST_ID, 99L)).isFalse();
    }

    @Test
    @DisplayName("isUserOwner - returns true for owner")
    void isUserOwner_trueForOwner() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThat(playlistService.isUserOwner(PLAYLIST_ID, USER_ID)).isTrue();
    }

    @Test
    @DisplayName("isUserOwner - returns false for non-owner")
    void isUserOwner_falseForNonOwner() {
        when(playlistRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(testPlaylist));

        assertThat(playlistService.isUserOwner(PLAYLIST_ID, 99L)).isFalse();
    }
}
