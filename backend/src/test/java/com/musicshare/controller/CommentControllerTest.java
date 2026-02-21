package com.musicshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicshare.dto.request.CreateCommentRequest;
import com.musicshare.dto.request.UpdateCommentRequest;
import com.musicshare.dto.response.UserResponse;
import com.musicshare.entity.Comment;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.security.CustomUserDetailsService;
import com.musicshare.security.JwtAuthenticationFilter;
import com.musicshare.security.JwtTokenProvider;
import com.musicshare.service.CommentService;
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

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CommentController Integration Tests")
class CommentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CommentService commentService;
    @MockBean private UserService userService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponse mockUserResponse;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse();
        mockUserResponse.setId(1L);
        mockUserResponse.setUsername("testuser");
        mockUserResponse.setEmail("test@example.com");
        mockUserResponse.setNickname("Test User");
        mockUserResponse.setEnabled(true);
        mockUserResponse.setRoles(Collections.emptySet());

        mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setContent("This is a great song!");
        mockComment.setUserId(1L);
        mockComment.setTargetType(Comment.TargetType.MUSIC);
        mockComment.setTargetId(10L);
        mockComment.setLikeCount(0);
        mockComment.setReplyCount(0);
    }

    // ==================== CREATE COMMENT ====================

    @Test
    @DisplayName("POST /api/comments - success returns 201 with comment data")
    void createComment_success() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("This is a great song!");
        request.setTargetType(Comment.TargetType.MUSIC);
        request.setTargetId(10L);

        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        when(commentService.createComment(anyString(), any(), anyLong(), any()))
                .thenReturn(mockComment);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("This is a great song!"))
                .andExpect(jsonPath("$.message").value("Comment created successfully"));
    }

    @Test
    @DisplayName("POST /api/comments - missing required fields returns 400")
    void createComment_missingFields() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/comments - reply to another comment success")
    void createComment_reply() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("I agree with you!");
        request.setTargetType(Comment.TargetType.MUSIC);
        request.setTargetId(10L);
        request.setParentCommentId(5L);

        Comment replyComment = new Comment();
        replyComment.setId(2L);
        replyComment.setContent("I agree with you!");
        replyComment.setUserId(1L);
        replyComment.setTargetType(Comment.TargetType.MUSIC);
        replyComment.setTargetId(10L);
        replyComment.setParentCommentId(5L);
        replyComment.setLikeCount(0);
        replyComment.setReplyCount(0);

        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        when(commentService.createComment(anyString(), any(), anyLong(), eq(5L)))
                .thenReturn(replyComment);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.parentCommentId").value(5));
    }

    // ==================== UPDATE COMMENT ====================

    @Test
    @DisplayName("PUT /api/comments/{id} - success returns 200 with updated comment")
    void updateComment_success() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated comment content");

        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setContent("Updated comment content");
        updatedComment.setUserId(1L);
        updatedComment.setTargetType(Comment.TargetType.MUSIC);
        updatedComment.setTargetId(10L);
        updatedComment.setLikeCount(0);
        updatedComment.setReplyCount(0);

        when(commentService.updateComment(eq(1L), eq("Updated comment content")))
                .thenReturn(updatedComment);

        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("Updated comment content"))
                .andExpect(jsonPath("$.message").value("Comment updated successfully"));
    }

    @Test
    @DisplayName("PUT /api/comments/{id} - permission denied returns error 4009")
    void updateComment_permissionDenied() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Trying to edit someone else's comment");

        when(commentService.updateComment(anyLong(), anyString()))
                .thenThrow(new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED));

        mockMvc.perform(put("/api/comments/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4009));
    }

    // ==================== DELETE COMMENT ====================

    @Test
    @DisplayName("DELETE /api/comments/{id} - success returns 200")
    void deleteComment_success() throws Exception {
        doNothing().when(commentService).deleteComment(eq(1L));

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} - permission denied returns error 4009")
    void deleteComment_permissionDenied() throws Exception {
        doThrow(new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED))
                .when(commentService).deleteComment(anyLong());

        mockMvc.perform(delete("/api/comments/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4009));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} - comment not found returns error 4001")
    void deleteComment_notFound() throws Exception {
        doThrow(new BusinessException(ErrorCode.COMMENT_NOT_FOUND))
                .when(commentService).deleteComment(anyLong());

        mockMvc.perform(delete("/api/comments/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001));
    }

    // ==================== GET COMMENT BY ID ====================

    @Test
    @DisplayName("GET /api/comments/{id} - success returns 200 with comment")
    void getCommentById_success() throws Exception {
        when(commentService.getCommentById(eq(1L))).thenReturn(mockComment);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("This is a great song!"));
    }

    @Test
    @DisplayName("GET /api/comments/{id} - not found returns error 4001")
    void getCommentById_notFound() throws Exception {
        when(commentService.getCommentById(eq(999L)))
                .thenThrow(new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        mockMvc.perform(get("/api/comments/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001));
    }

    // ==================== GET COMMENTS BY TARGET ====================

    @Test
    @DisplayName("GET /api/comments/target - returns 200 with comments for target")
    void getCommentsByTarget_success() throws Exception {
        PageImpl<Comment> page = new PageImpl<>(List.of(mockComment));
        when(commentService.getCommentsByTarget(eq(Comment.TargetType.MUSIC), eq(10L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/comments/target")
                        .param("targetType", "MUSIC")
                        .param("targetId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].content").value("This is a great song!"));
    }

    @Test
    @DisplayName("GET /api/comments/target - playlist comments returns 200")
    void getCommentsByTarget_playlist() throws Exception {
        Comment playlistComment = new Comment();
        playlistComment.setId(2L);
        playlistComment.setContent("Great playlist!");
        playlistComment.setUserId(1L);
        playlistComment.setTargetType(Comment.TargetType.PLAYLIST);
        playlistComment.setTargetId(5L);
        playlistComment.setLikeCount(0);
        playlistComment.setReplyCount(0);

        PageImpl<Comment> page = new PageImpl<>(List.of(playlistComment));
        when(commentService.getCommentsByTarget(eq(Comment.TargetType.PLAYLIST), eq(5L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/comments/target")
                        .param("targetType", "PLAYLIST")
                        .param("targetId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("Great playlist!"));
    }

    // ==================== GET REPLIES ====================

    @Test
    @DisplayName("GET /api/comments/{id}/replies - returns 200 with replies")
    void getReplies_success() throws Exception {
        Comment reply = new Comment();
        reply.setId(3L);
        reply.setContent("I agree!");
        reply.setUserId(2L);
        reply.setTargetType(Comment.TargetType.MUSIC);
        reply.setTargetId(10L);
        reply.setParentCommentId(1L);
        reply.setLikeCount(0);
        reply.setReplyCount(0);

        PageImpl<Comment> page = new PageImpl<>(List.of(reply));
        when(commentService.getReplies(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/comments/1/replies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].parentCommentId").value(1));
    }

    @Test
    @DisplayName("GET /api/comments/{id}/replies - no replies returns empty page")
    void getReplies_empty() throws Exception {
        PageImpl<Comment> page = new PageImpl<>(Collections.emptyList());
        when(commentService.getReplies(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/comments/1/replies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    // ==================== MY COMMENTS ====================

    @Test
    @DisplayName("GET /api/comments/my - returns current user's comments")
    void getMyComments_success() throws Exception {
        when(userService.getUserProfile(any())).thenReturn(mockUserResponse);
        PageImpl<Comment> page = new PageImpl<>(List.of(mockComment));
        when(commentService.getUserComments(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/comments/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].content").value("This is a great song!"));
    }
}
