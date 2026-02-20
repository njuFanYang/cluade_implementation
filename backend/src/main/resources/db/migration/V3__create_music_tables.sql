-- ===================================================================================
-- MusicShare Database Migration
-- Version: V3
-- Description: Create music-related tables (genres, albums, music, playlists)
-- Author: MusicShare Team
-- Date: 2026-02-20
-- ===================================================================================

-- ----------------------------
-- Table: genres (音乐类型)
-- ----------------------------
CREATE TABLE IF NOT EXISTS genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Genre name in English',
    name_zh VARCHAR(50) COMMENT 'Genre name in Chinese',
    description VARCHAR(500) COMMENT 'Genre description',
    icon VARCHAR(255) COMMENT 'Icon URL or identifier',
    music_count INT NOT NULL DEFAULT 0 COMMENT 'Number of music tracks',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    INDEX idx_name (name),
    INDEX idx_music_count (music_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Music genres table';

-- ----------------------------
-- Table: albums (专辑)
-- ----------------------------
CREATE TABLE IF NOT EXISTS albums (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    title VARCHAR(200) NOT NULL COMMENT 'Album title',
    artist VARCHAR(100) NOT NULL COMMENT 'Album artist',
    cover_image VARCHAR(500) COMMENT 'Cover image URL',
    description TEXT COMMENT 'Album description',
    release_date DATE COMMENT 'Release date',
    creator_id BIGINT NOT NULL COMMENT 'Creator user ID',
    music_count INT NOT NULL DEFAULT 0 COMMENT 'Number of tracks',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX idx_title (title),
    INDEX idx_artist (artist),
    INDEX idx_creator (creator_id),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Albums table';

-- ----------------------------
-- Table: music (音乐主表)
-- ----------------------------
CREATE TABLE IF NOT EXISTS music (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    title VARCHAR(200) NOT NULL COMMENT 'Music title',
    artist VARCHAR(100) NOT NULL COMMENT 'Artist name',
    album_id BIGINT COMMENT 'Album ID',
    genre_id BIGINT NOT NULL COMMENT 'Genre ID',
    duration INT NOT NULL COMMENT 'Duration in seconds',
    file_path VARCHAR(500) NOT NULL UNIQUE COMMENT 'File storage path',
    file_name VARCHAR(255) NOT NULL COMMENT 'Original file name',
    file_size BIGINT NOT NULL COMMENT 'File size in bytes',
    file_format VARCHAR(10) NOT NULL COMMENT 'File format (mp3, flac, etc)',
    cover_image VARCHAR(500) COMMENT 'Cover image URL',
    description TEXT COMMENT 'Music description',
    lyrics TEXT COMMENT 'Lyrics',
    release_date DATE COMMENT 'Release date',
    uploader_id BIGINT NOT NULL COMMENT 'Uploader user ID',
    play_count INT NOT NULL DEFAULT 0 COMMENT 'Play count',
    like_count INT NOT NULL DEFAULT 0 COMMENT 'Like count',
    comment_count INT NOT NULL DEFAULT 0 COMMENT 'Comment count',
    download_count INT NOT NULL DEFAULT 0 COMMENT 'Download count',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Status: PENDING, APPROVED, REJECTED',
    is_public BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Public visibility',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX idx_title (title),
    INDEX idx_artist (artist),
    INDEX idx_genre (genre_id),
    INDEX idx_album (album_id),
    INDEX idx_uploader (uploader_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at),
    INDEX idx_play_count (play_count),
    INDEX idx_public (is_public),
    FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE SET NULL,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE RESTRICT,
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Music table';

-- ----------------------------
-- Table: playlists (歌单)
-- ----------------------------
CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    name VARCHAR(100) NOT NULL COMMENT 'Playlist name',
    description VARCHAR(500) COMMENT 'Playlist description',
    cover_image VARCHAR(500) COMMENT 'Cover image URL',
    creator_id BIGINT NOT NULL COMMENT 'Creator user ID',
    is_public BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Public visibility',
    music_count INT NOT NULL DEFAULT 0 COMMENT 'Number of tracks',
    play_count INT NOT NULL DEFAULT 0 COMMENT 'Play count',
    like_count INT NOT NULL DEFAULT 0 COMMENT 'Like count',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX idx_name (name),
    INDEX idx_creator (creator_id),
    INDEX idx_public (is_public),
    INDEX idx_created (created_at),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Playlists table';

-- ----------------------------
-- Table: playlist_music (歌单-音乐关联)
-- ----------------------------
CREATE TABLE IF NOT EXISTS playlist_music (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    playlist_id BIGINT NOT NULL COMMENT 'Playlist ID',
    music_id BIGINT NOT NULL COMMENT 'Music ID',
    position INT NOT NULL COMMENT 'Position in playlist',
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Added time',
    UNIQUE KEY uk_playlist_music (playlist_id, music_id),
    INDEX idx_playlist (playlist_id),
    INDEX idx_music (music_id),
    INDEX idx_position (playlist_id, position),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (music_id) REFERENCES music(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Playlist-Music association table';
