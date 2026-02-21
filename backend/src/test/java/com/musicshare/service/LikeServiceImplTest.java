package com.musicshare.service;

import com.musicshare.entity.*;
import com.musicshare.exception.BusinessException;
import com.musicshare.repository.*;
import com.musicshare.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeService Unit Tests")
class LikeServiceImplTest {

    @Mock private LikeRepository likeRepository;
    @Mock private MusicRepository musicRepository;
    @Mock private PlaylistRepository playlistRepository;
    @Mock private CommentRepository commentRepository;
    @InjectMocks private LikeServiceImpl likeService;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        var principal = new User(USER_ID.toString(), "", authorities);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== LIKE TESTS ====================

    @Test
    @DisplayName("like music - success increments like count")
    void like_music_success() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L)).thenReturn(false);
        when(musicRepository.existsById(10L)).thenReturn(true);
        Like savedLike = new Like(USER_ID, Like.TargetType.MUSIC, 10L);
        when(likeRepository.save(any(Like.class))).thenReturn(savedLike);

        Music music = new Music();
        music.setId(10L);
        music.setLikeCount(5);
        when(musicRepository.findById(10L)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        Like result = likeService.like(Like.TargetType.MUSIC, 10L);

        assertThat(result).isNotNull();
        verify(likeRepository).save(any(Like.class));
        verify(musicRepository).save(argThat(m -> m.getLikeCount() == 6));
    }

    @Test
    @DisplayName("like playlist - success increments like count")
    void like_playlist_success() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.PLAYLIST, 20L)).thenReturn(false);
        when(playlistRepository.existsById(20L)).thenReturn(true);
        Like savedLike = new Like(USER_ID, Like.TargetType.PLAYLIST, 20L);
        when(likeRepository.save(any(Like.class))).thenReturn(savedLike);

        Playlist playlist = new Playlist();
        playlist.setId(20L);
        playlist.setLikeCount(3);
        when(playlistRepository.findById(20L)).thenReturn(Optional.of(playlist));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);

        Like result = likeService.like(Like.TargetType.PLAYLIST, 20L);

        assertThat(result).isNotNull();
        verify(playlistRepository).save(argThat(p -> p.getLikeCount() == 4));
    }

    @Test
    @DisplayName("like comment - success increments like count")
    void like_comment_success() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.COMMENT, 30L)).thenReturn(false);
        when(commentRepository.existsById(30L)).thenReturn(true);
        Like savedLike = new Like(USER_ID, Like.TargetType.COMMENT, 30L);
        when(likeRepository.save(any(Like.class))).thenReturn(savedLike);

        Comment comment = new Comment();
        comment.setId(30L);
        comment.setLikeCount(0);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Like result = likeService.like(Like.TargetType.COMMENT, 30L);

        assertThat(result).isNotNull();
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("like - throws ALREADY_LIKED when duplicate")
    void like_throwsAlreadyLiked() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L)).thenReturn(true);

        assertThatThrownBy(() -> likeService.like(Like.TargetType.MUSIC, 10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Already liked");
    }

    @Test
    @DisplayName("like - throws TARGET_NOT_FOUND when music doesn't exist")
    void like_throwsTargetNotFound_whenMusicNotExists() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 99L)).thenReturn(false);
        when(musicRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> likeService.like(Like.TargetType.MUSIC, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Target resource not found");
    }

    @Test
    @DisplayName("like - throws TARGET_NOT_FOUND when playlist doesn't exist")
    void like_throwsTargetNotFound_whenPlaylistNotExists() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.PLAYLIST, 99L)).thenReturn(false);
        when(playlistRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> likeService.like(Like.TargetType.PLAYLIST, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Target resource not found");
    }

    // ==================== UNLIKE TESTS ====================

    @Test
    @DisplayName("unlike music - success decrements like count")
    void unlike_music_success() {
        Like existingLike = new Like(USER_ID, Like.TargetType.MUSIC, 10L);
        when(likeRepository.findByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L))
                .thenReturn(Optional.of(existingLike));

        Music music = new Music();
        music.setId(10L);
        music.setLikeCount(5);
        when(musicRepository.findById(10L)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        assertThatCode(() -> likeService.unlike(Like.TargetType.MUSIC, 10L)).doesNotThrowAnyException();
        verify(likeRepository).delete(existingLike);
        verify(musicRepository).save(argThat(m -> m.getLikeCount() == 4));
    }

    @Test
    @DisplayName("unlike - like count does not go below 0")
    void unlike_likeCountNotBelowZero() {
        Like existingLike = new Like(USER_ID, Like.TargetType.MUSIC, 10L);
        when(likeRepository.findByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L))
                .thenReturn(Optional.of(existingLike));

        Music music = new Music();
        music.setId(10L);
        music.setLikeCount(0);
        when(musicRepository.findById(10L)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        assertThatCode(() -> likeService.unlike(Like.TargetType.MUSIC, 10L)).doesNotThrowAnyException();
        verify(musicRepository).save(argThat(m -> m.getLikeCount() == 0));
    }

    @Test
    @DisplayName("unlike - throws NOT_LIKED_YET when not liked")
    void unlike_throwsNotLikedYet() {
        when(likeRepository.findByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.unlike(Like.TargetType.MUSIC, 10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Not liked yet");
    }

    // ==================== IS LIKED TESTS ====================

    @Test
    @DisplayName("isLiked - returns true when liked")
    void isLiked_returnsTrue() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L)).thenReturn(true);

        assertThat(likeService.isLiked(Like.TargetType.MUSIC, 10L)).isTrue();
    }

    @Test
    @DisplayName("isLiked - returns false when not liked")
    void isLiked_returnsFalse() {
        when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(USER_ID, Like.TargetType.MUSIC, 10L)).thenReturn(false);

        assertThat(likeService.isLiked(Like.TargetType.MUSIC, 10L)).isFalse();
    }

    // ==================== GET LIKE COUNT TESTS ====================

    @Test
    @DisplayName("getLikeCount - returns correct count")
    void getLikeCount_returnsCount() {
        when(likeRepository.countByTargetTypeAndTargetId(Like.TargetType.MUSIC, 10L)).thenReturn(42L);

        assertThat(likeService.getLikeCount(Like.TargetType.MUSIC, 10L)).isEqualTo(42L);
    }

    @Test
    @DisplayName("getLikeCount - returns 0 for no likes")
    void getLikeCount_returnsZero() {
        when(likeRepository.countByTargetTypeAndTargetId(Like.TargetType.PLAYLIST, 20L)).thenReturn(0L);

        assertThat(likeService.getLikeCount(Like.TargetType.PLAYLIST, 20L)).isEqualTo(0L);
    }
}
