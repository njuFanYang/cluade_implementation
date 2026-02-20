-- =====================================================
-- Social Module Tables
-- Version: 5
-- Description: Create tables for comments, likes, and follows
-- Author: MusicShare Team
-- Created: 2026-02-20
-- =====================================================

-- =====================================================
-- Comments Table
-- =====================================================
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(1000) NOT NULL COMMENT 'Comment content',
    user_id BIGINT NOT NULL COMMENT 'Commenter user ID',
    target_type VARCHAR(20) NOT NULL COMMENT 'Target type: MUSIC, PLAYLIST',
    target_id BIGINT NOT NULL COMMENT 'Target ID (music_id or playlist_id)',
    parent_comment_id BIGINT DEFAULT NULL COMMENT 'Parent comment ID for replies',
    like_count INT NOT NULL DEFAULT 0 COMMENT 'Number of likes',
    reply_count INT NOT NULL DEFAULT 0 COMMENT 'Number of replies',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',

    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,

    -- Indexes
    INDEX idx_target (target_type, target_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_comment_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comments table';

-- =====================================================
-- Likes Table
-- =====================================================
CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'User who liked',
    target_type VARCHAR(20) NOT NULL COMMENT 'Target type: MUSIC, PLAYLIST, COMMENT',
    target_id BIGINT NOT NULL COMMENT 'Target ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',

    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Unique Constraint (one like per user per target)
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),

    -- Indexes
    INDEX idx_target (target_type, target_id),
    INDEX idx_user (user_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Likes table';

-- =====================================================
-- Follows Table
-- =====================================================
CREATE TABLE follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL COMMENT 'Follower user ID (who follows)',
    following_id BIGINT NOT NULL COMMENT 'Following user ID (being followed)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',

    -- Foreign Keys
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Unique Constraint (one follow relationship per pair)
    UNIQUE KEY uk_follower_following (follower_id, following_id),

    -- Check Constraint (cannot follow yourself)
    CONSTRAINT chk_no_self_follow CHECK (follower_id != following_id),

    -- Indexes
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Follow relationships table';

-- =====================================================
-- Add comment_count columns to existing tables
-- =====================================================

-- Add comment_count to music table
ALTER TABLE music ADD COLUMN comment_count INT NOT NULL DEFAULT 0 COMMENT 'Number of comments' AFTER download_count;

-- Add comment_count to playlists table
ALTER TABLE playlists ADD COLUMN comment_count INT NOT NULL DEFAULT 0 COMMENT 'Number of comments' AFTER like_count;

-- =====================================================
-- Add follower/following counts to users table
-- =====================================================

-- Add follower_count to users table
ALTER TABLE users ADD COLUMN follower_count INT NOT NULL DEFAULT 0 COMMENT 'Number of followers' AFTER music_count;

-- Add following_count to users table
ALTER TABLE users ADD COLUMN following_count INT NOT NULL DEFAULT 0 COMMENT 'Number of following' AFTER follower_count;
