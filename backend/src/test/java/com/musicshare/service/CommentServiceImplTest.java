package com.musicshare.service;

import com.musicshare.entity.Comment;
import com.musicshare.entity.Music;
import com.musicshare.entity.Playlist;
import com.musicshare.exception.BusinessException;
import com.musicshare.repository.CommentRepository;
import com.musicshare.repository.MusicRepository;
import com.musicshare.repository.PlaylistRepository;
import com.musicshare.service.impl.CommentServiceImpl;
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
@DisplayName("CommentService Unit Tests")
class CommentServiceImplTest {

    @Mock private CommentRepository commentRepository;
    @Mock private MusicRepository musicRepository;
    @Mock private PlaylistRepository playlistRepository;
    @InjectMocks private CommentServiceImpl commentService;

    private static final Long USER_ID = 1L;
    private static final Long MUSIC_ID = 10L;
    private static final Long PLAYLIST_ID = 20L;
    private static final Long COMMENT_ID = 100L;

    @BeforeEach
    void setUp() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        var principal = new org.springframework.security.core.userdetails.User(USER_ID.toString(), "", authorities);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== CREATE COMMENT TESTS ====================

    @Test
    @DisplayName("createComment - success on music target")
    void createComment_onMusic_success() {
        when(musicRepository.existsById(MUSIC_ID)).thenReturn(true);
        Comment savedComment = new Comment();
        savedComment.setId(COMMENT_ID);
        savedComment.setContent("Great song!");
        savedComment.setUserId(USER_ID);
        savedComment.setTargetType(Comment.TargetType.MUSIC);
        savedComment.setTargetId(MUSIC_ID);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Music music = new Music();
        music.setId(MUSIC_ID);
        music.setCommentCount(5);
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        Comment result = commentService.createComment("Great song!", Comment.TargetType.MUSIC, MUSIC_ID, null);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Great song!");
        verify(commentRepository).save(any(Comment.class));
        verify(musicRepository).save(argThat(m -> m.getCommentCount() == 6));
    }

    @Test
    @DisplayName("createComment - success on playlist target")
    void createComment_onPlaylist_success() {
        when(playlistRepository.existsById(PLAYLIST_ID)).thenReturn(true);
        Comment savedComment = new Comment();
        savedComment.setId(COMMENT_ID);
        savedComment.setContent("Nice playlist!");
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Comment result = commentService.createComment("Nice playlist!", Comment.TargetType.PLAYLIST, PLAYLIST_ID, null);

        assertThat(result).isNotNull();
        verify(commentRepository).save(any(Comment.class));
        // Playlist has no commentCount field, so no playlist save expected
        verify(playlistRepository, never()).save(any(Playlist.class));
    }

    @Test
    @DisplayName("createComment - as reply increments parent reply count")
    void createComment_reply_incrementsParentReplyCount() {
        when(musicRepository.existsById(MUSIC_ID)).thenReturn(true);

        Comment parentComment = new Comment();
        parentComment.setId(50L);
        parentComment.setTargetType(Comment.TargetType.MUSIC);
        parentComment.setTargetId(MUSIC_ID);
        parentComment.setReplyCount(2);
        when(commentRepository.findById(50L)).thenReturn(Optional.of(parentComment));

        Comment savedComment = new Comment();
        savedComment.setId(COMMENT_ID);
        savedComment.setContent("Reply!");
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Music music = new Music();
        music.setId(MUSIC_ID);
        music.setCommentCount(5);
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        Comment result = commentService.createComment("Reply!", Comment.TargetType.MUSIC, MUSIC_ID, 50L);

        assertThat(result).isNotNull();
        // Two saves: one for new comment, one for parent reply count
        verify(commentRepository, times(2)).save(any(Comment.class));
        verify(commentRepository).save(argThat(c -> c.getId() != null && c.getId().equals(50L) && c.getReplyCount() == 3));
    }

    @Test
    @DisplayName("createComment - throws TARGET_NOT_FOUND when music doesn't exist")
    void createComment_throwsTargetNotFound_music() {
        when(musicRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> commentService.createComment("test", Comment.TargetType.MUSIC, 99L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Target resource not found");
    }

    @Test
    @DisplayName("createComment - throws TARGET_NOT_FOUND when playlist doesn't exist")
    void createComment_throwsTargetNotFound_playlist() {
        when(playlistRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> commentService.createComment("test", Comment.TargetType.PLAYLIST, 99L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Target resource not found");
    }

    @Test
    @DisplayName("createComment - throws COMMENT_NOT_FOUND for invalid parent comment")
    void createComment_throwsCommentNotFound_invalidParent() {
        when(musicRepository.existsById(MUSIC_ID)).thenReturn(true);
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment("reply", Comment.TargetType.MUSIC, MUSIC_ID, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("createComment - throws INVALID_PARAMETER when parent is on different target")
    void createComment_throwsInvalidParam_parentOnDifferentTarget() {
        when(musicRepository.existsById(MUSIC_ID)).thenReturn(true);

        Comment parentComment = new Comment();
        parentComment.setId(50L);
        parentComment.setTargetType(Comment.TargetType.MUSIC);
        parentComment.setTargetId(999L); // different music ID
        when(commentRepository.findById(50L)).thenReturn(Optional.of(parentComment));

        assertThatThrownBy(() -> commentService.createComment("reply", Comment.TargetType.MUSIC, MUSIC_ID, 50L))
                .isInstanceOf(BusinessException.class);
    }

    // ==================== UPDATE COMMENT TESTS ====================

    @Test
    @DisplayName("updateComment - owner can update their comment")
    void updateComment_ownerCanUpdate() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setUserId(USER_ID);
        comment.setContent("Old content");
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.updateComment(COMMENT_ID, "New content");

        assertThat(result).isNotNull();
        verify(commentRepository).save(argThat(c -> "New content".equals(c.getContent())));
    }

    @Test
    @DisplayName("updateComment - throws COMMENT_PERMISSION_DENIED for non-owner non-admin")
    void updateComment_throwsPermissionDenied() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setUserId(99L); // different user
        comment.setContent("Someone else's comment");
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(COMMENT_ID, "New content"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    @DisplayName("updateComment - throws COMMENT_NOT_FOUND for nonexistent comment")
    void updateComment_throwsCommentNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateComment(999L, "content"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Comment not found");
    }

    // ==================== DELETE COMMENT TESTS ====================

    @Test
    @DisplayName("deleteComment - owner can delete and decrements music comment count")
    void deleteComment_ownerCanDelete_musicTarget() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setUserId(USER_ID);
        comment.setTargetType(Comment.TargetType.MUSIC);
        comment.setTargetId(MUSIC_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        Music music = new Music();
        music.setId(MUSIC_ID);
        music.setCommentCount(3);
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        assertThatCode(() -> commentService.deleteComment(COMMENT_ID)).doesNotThrowAnyException();
        verify(commentRepository).delete(comment);
        verify(musicRepository).save(argThat(m -> m.getCommentCount() == 2));
    }

    @Test
    @DisplayName("deleteComment - reply decrements parent reply count")
    void deleteComment_reply_decrementsParentReplyCount() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setUserId(USER_ID);
        comment.setTargetType(Comment.TargetType.MUSIC);
        comment.setTargetId(MUSIC_ID);
        comment.setParentCommentId(50L);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        Comment parentComment = new Comment();
        parentComment.setId(50L);
        parentComment.setReplyCount(3);
        when(commentRepository.findById(50L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(parentComment);

        Music music = new Music();
        music.setId(MUSIC_ID);
        music.setCommentCount(3);
        when(musicRepository.findById(MUSIC_ID)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);

        assertThatCode(() -> commentService.deleteComment(COMMENT_ID)).doesNotThrowAnyException();
        verify(commentRepository).save(argThat(c -> c.getId().equals(50L) && c.getReplyCount() == 2));
    }

    @Test
    @DisplayName("deleteComment - throws CANNOT_DELETE_COMMENT for non-owner")
    void deleteComment_throwsCannotDelete_nonOwner() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setUserId(99L); // different user
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete this comment");
    }

    @Test
    @DisplayName("deleteComment - throws COMMENT_NOT_FOUND for nonexistent comment")
    void deleteComment_throwsCommentNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Comment not found");
    }

    // ==================== GET COMMENT TESTS ====================

    @Test
    @DisplayName("getCommentById - returns comment when found")
    void getCommentById_returnsComment() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setContent("Test comment");
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(COMMENT_ID);

        assertThat(result.getId()).isEqualTo(COMMENT_ID);
        assertThat(result.getContent()).isEqualTo("Test comment");
    }

    @Test
    @DisplayName("getCommentById - throws COMMENT_NOT_FOUND")
    void getCommentById_throwsNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("getReplies - throws COMMENT_NOT_FOUND when parent not found")
    void getReplies_throwsCommentNotFound() {
        when(commentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> commentService.getReplies(999L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("countCommentsByTarget - returns count from repository")
    void countCommentsByTarget_returnsCount() {
        when(commentRepository.countTopLevelByTarget(Comment.TargetType.MUSIC, MUSIC_ID)).thenReturn(7L);

        assertThat(commentService.countCommentsByTarget(Comment.TargetType.MUSIC, MUSIC_ID)).isEqualTo(7L);
    }
}
