package com.musicshare.repository;

import com.musicshare.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comment repository interface
 *
 * @description Data access layer for Comment entity
 * @author MusicShare Team
 * @version 1.0.0
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find top-level comments by target (not replies)
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @param pageable Pagination info
     * @return Page of comments
     */
    @Query("SELECT c FROM Comment c " +
           "WHERE c.targetType = :targetType " +
           "AND c.targetId = :targetId " +
           "AND c.parentCommentId IS NULL")
    Page<Comment> findByTarget(
            @Param("targetType") Comment.TargetType targetType,
            @Param("targetId") Long targetId,
            Pageable pageable
    );

    /**
     * Find comments by target with user info (top-level only)
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @param pageable Pagination info
     * @return Page of comments with user
     */
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.targetType = :targetType " +
           "AND c.targetId = :targetId " +
           "AND c.parentCommentId IS NULL")
    Page<Comment> findByTargetWithUser(
            @Param("targetType") Comment.TargetType targetType,
            @Param("targetId") Long targetId,
            Pageable pageable
    );

    /**
     * Find replies to a comment
     *
     * @param parentCommentId Parent comment ID
     * @param pageable Pagination info
     * @return Page of replies
     */
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.parentCommentId = :parentCommentId")
    Page<Comment> findRepliesByParentId(
            @Param("parentCommentId") Long parentCommentId,
            Pageable pageable
    );

    /**
     * Find replies to a comment (list)
     *
     * @param parentCommentId Parent comment ID
     * @return List of replies
     */
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.parentCommentId = :parentCommentId " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentCommentId") Long parentCommentId);

    /**
     * Find comments by user
     *
     * @param userId User ID
     * @param pageable Pagination info
     * @return Page of comments
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);

    /**
     * Count comments by target
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return Comment count
     */
    long countByTargetTypeAndTargetId(
            Comment.TargetType targetType,
            Long targetId
    );

    /**
     * Count top-level comments by target (excluding replies)
     *
     * @param targetType Target type
     * @param targetId Target ID
     * @return Comment count
     */
    @Query("SELECT COUNT(c) FROM Comment c " +
           "WHERE c.targetType = :targetType " +
           "AND c.targetId = :targetId " +
           "AND c.parentCommentId IS NULL")
    long countTopLevelByTarget(
            @Param("targetType") Comment.TargetType targetType,
            @Param("targetId") Long targetId
    );

    /**
     * Count replies by parent comment
     *
     * @param parentCommentId Parent comment ID
     * @return Reply count
     */
    long countByParentCommentId(Long parentCommentId);

    /**
     * Delete comments by target
     *
     * @param targetType Target type
     * @param targetId Target ID
     */
    void deleteByTargetTypeAndTargetId(
            Comment.TargetType targetType,
            Long targetId
    );

    /**
     * Delete comments by user
     *
     * @param userId User ID
     */
    void deleteByUserId(Long userId);
}
