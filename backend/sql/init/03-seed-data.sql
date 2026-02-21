-- Seed Data for MusicShare Development/Testing
-- Created: 2026-02-21
-- Accounts: admin / admin123 (ADMIN + MUSICIAN), test1 / test123 (USER)
-- Music:    4 tracks (pre-approved) — files must exist at backend/music/seed/

USE musicshare;

-- ─── Genres ────────────────────────────────────────────────────────────────
INSERT IGNORE INTO genres (id, name, name_zh, description, music_count, created_at)
VALUES
    (1, 'Pop',        '流行',   '流行音乐，旋律朗朗上口',              0, NOW()),
    (2, 'Rock',       '摇滚',   '摇滚乐，充满力量与激情',              0, NOW()),
    (3, 'Folk',       '民谣',   '民谣/民族风格，贴近生活',             0, NOW()),
    (4, 'Electronic', '电子',   '电子音乐，合成器与节拍',              0, NOW()),
    (5, 'Classical',  '古典',   '古典音乐，管弦乐与钢琴',              0, NOW()),
    (6, 'Hip-Hop',    '嘻哈',   'Hip-Hop/说唱，节奏与押韵',            0, NOW()),
    (7, 'R&B',        'R&B',    '节奏布鲁斯，灵魂与情感',              0, NOW()),
    (8, 'Jazz',       '爵士',   '爵士乐，即兴演奏与蓝调',              0, NOW());

-- ─── Users ─────────────────────────────────────────────────────────────────
-- Passwords are BCrypt-encoded (cost=10)
-- admin123 → $2a$10$sXEWZYLGeDu8sQmqXSFMoOIMvQllvs836ESfBdJ2UiDlngo6cPYPG
-- test123  → $2a$10$1hH1gbOLDT3hE/KOrxY6euzj7JM3kPUiu4h6cd60iXnU./etSiv1m

INSERT IGNORE INTO users
    (id, username, email, password, nickname, enabled, locked,
     followers_count, following_count, music_count, playlist_count,
     created_at, updated_at)
VALUES
    (1, 'admin', 'admin@example.com',
     '$2a$10$sXEWZYLGeDu8sQmqXSFMoOIMvQllvs836ESfBdJ2UiDlngo6cPYPG',
     'Administrator', TRUE, FALSE, 0, 0, 4, 0, NOW(), NOW()),

    (2, 'test1', 'test1@example.com',
     '$2a$10$1hH1gbOLDT3hE/KOrxY6euzj7JM3kPUiu4h6cd60iXnU./etSiv1m',
     'Test User 1', TRUE, FALSE, 0, 0, 0, 0, NOW(), NOW());

-- ─── User Roles ────────────────────────────────────────────────────────────
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'ROLE_ADMIN'
UNION ALL
SELECT 1, id FROM roles WHERE name = 'ROLE_MUSICIAN'
UNION ALL
SELECT 2, id FROM roles WHERE name = 'ROLE_USER';

-- ─── Music (pre-approved seed tracks) ──────────────────────────────────────
-- file_path is relative to backend working dir (backend/music/seed/)
-- Files are copied there by deploy.sh before the backend starts

INSERT IGNORE INTO music
    (id, title, artist, genre_id, duration,
     file_path, file_name, file_size, file_format,
     uploader_id, play_count, like_count, comment_count, download_count,
     status, is_public, created_at, updated_at)
VALUES
    (1, '晴天', '周杰伦', 1, 287,
     'music/seed/qingtian.mp3', '周杰伦 - 晴天(1).mp3', 10793305, 'mp3',
     1, 0, 0, 0, 0, 'APPROVED', TRUE, NOW(), NOW()),

    (2, '花海', '周杰伦', 1, 261,
     'music/seed/huahai.mp3', '周杰伦 - 花海(1).mp3', 10589150, 'mp3',
     1, 0, 0, 0, 0, 'APPROVED', TRUE, NOW(), NOW()),

    (3, '江湖之间', '曹雨航', 3, 222,
     'music/seed/jianghu.mp3', '曹雨航,朝歌夜弦+-+江湖之间.mp3', 8767717, 'mp3',
     1, 0, 0, 0, 0, 'APPROVED', TRUE, NOW(), NOW()),

    (4, '去年夏天', '王大毛', 3, 248,
     'music/seed/qunian_xiatian.mp3', '王大毛+-+去年夏天.mp3', 9929034, 'mp3',
     1, 0, 0, 0, 0, 'APPROVED', TRUE, NOW(), NOW());

-- ─── Sync genre music count ────────────────────────────────────────────────
UPDATE genres SET music_count = (
    SELECT COUNT(*) FROM music WHERE genre_id = genres.id AND status = 'APPROVED'
);

-- ─── Sync user music count ─────────────────────────────────────────────────
UPDATE users SET music_count = (
    SELECT COUNT(*) FROM music WHERE uploader_id = users.id
);
