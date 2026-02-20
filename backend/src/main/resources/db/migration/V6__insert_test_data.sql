-- ========================================
-- V6: Insert Test Data for MusicShare
-- ========================================
-- This script creates sample data for testing purposes
-- Author: MusicShare Team
-- Date: 2026-02-20
-- ========================================

-- Note: This assumes user IDs 1 and 2 exist from previous migrations
-- User 1 will be the main uploader, User 2 will be a secondary user

-- ========================================
-- Insert Test Albums
-- ========================================
INSERT INTO album (title, artist, release_date, cover_image, description, created_at, updated_at) VALUES
('Echoes of Tomorrow', 'The Synthwave Collective', '2024-03-15', 'https://picsum.photos/seed/album1/400/400', 'A journey through neon-lit streets and digital dreams', NOW(), NOW()),
('Acoustic Sessions Vol. 1', 'Sarah Mitchell', '2024-06-20', 'https://picsum.photos/seed/album2/400/400', 'Intimate acoustic performances recorded live', NOW(), NOW()),
('Urban Beats', 'DJ Phoenix', '2023-11-10', 'https://picsum.photos/seed/album3/400/400', 'Hip-hop beats for the modern city', NOW(), NOW()),
('Classical Reimagined', 'The Modern Orchestra', '2024-01-05', 'https://picsum.photos/seed/album4/400/400', 'Classical masterpieces with a contemporary twist', NOW(), NOW()),
('Summer Vibes', 'Tropical House Collective', '2024-07-01', 'https://picsum.photos/seed/album5/400/400', 'Feel-good summer anthems', NOW(), NOW());

-- ========================================
-- Insert Test Music Tracks
-- ========================================
INSERT INTO music (
    title, artist, album_id, genre_id, duration, file_path, file_format, file_size,
    cover_image, description, lyrics, release_date, is_public, play_count, like_count,
    uploader_id, status, created_at, updated_at
) VALUES
-- Pop Songs
('Midnight Dreams', 'Luna Rivers', 1, (SELECT id FROM genre WHERE name = 'Pop' LIMIT 1), 245, '/uploads/2024/02/midnight-dreams.mp3', 'mp3', 5840000, 'https://picsum.photos/seed/music1/300/300', 'An uplifting pop anthem about chasing your dreams', 'Verse 1: Under the stars we dance...\nChorus: Midnight dreams come alive...', '2024-02-15', TRUE, 1523, 234, 1, 'APPROVED', NOW(), NOW()),
('Neon Lights', 'The Synthwave Collective', 1, (SELECT id FROM genre WHERE name = 'Electronic' LIMIT 1), 198, '/uploads/2024/03/neon-lights.mp3', 'mp3', 4720000, 'https://picsum.photos/seed/music2/300/300', 'Synthwave track with retro vibes', NULL, '2024-03-15', TRUE, 2341, 445, 1, 'APPROVED', NOW(), NOW()),
('Summer Love', 'Tropical House Collective', 5, (SELECT id FROM genre WHERE name = 'Pop' LIMIT 1), 223, '/uploads/2024/07/summer-love.mp3', 'mp3', 5320000, 'https://picsum.photos/seed/music3/300/300', 'Perfect summer romance song', 'Verse 1: Walking on the beach...\nChorus: This summer love...', '2024-07-01', TRUE, 3456, 567, 1, 'APPROVED', NOW(), NOW()),

-- Rock Songs
('Thunder Road', 'The Rock Legends', NULL, (SELECT id FROM genre WHERE name = 'Rock' LIMIT 1), 267, '/uploads/2024/01/thunder-road.mp3', 'mp3', 6360000, 'https://picsum.photos/seed/music4/300/300', 'Hard-hitting rock anthem', NULL, '2024-01-20', TRUE, 2890, 378, 1, 'APPROVED', NOW(), NOW()),
('Electric Storm', 'Voltage Band', NULL, (SELECT id FROM genre WHERE name = 'Rock' LIMIT 1), 301, '/uploads/2023/12/electric-storm.mp3', 'mp3', 7180000, 'https://picsum.photos/seed/music5/300/300', 'High-energy rock track', NULL, '2023-12-10', TRUE, 1876, 289, 2, 'APPROVED', NOW(), NOW()),

-- Hip-Hop Songs
('City Streets', 'DJ Phoenix', 3, (SELECT id FROM genre WHERE name = 'Hip-Hop' LIMIT 1), 189, '/uploads/2023/11/city-streets.mp3', 'mp3', 4510000, 'https://picsum.photos/seed/music6/300/300', 'Urban hip-hop with smooth flow', 'Verse 1: Walking through the city...\nVerse 2: Concrete jungle life...', '2023-11-10', TRUE, 4521, 678, 1, 'APPROVED', NOW(), NOW()),
('Beat Drop', 'MC Rhythm', 3, (SELECT id FROM genre WHERE name = 'Hip-Hop' LIMIT 1), 176, '/uploads/2024/04/beat-drop.mp3', 'mp3', 4200000, 'https://picsum.photos/seed/music7/300/300', 'Heavy bass hip-hop track', NULL, '2024-04-05', TRUE, 3234, 512, 2, 'APPROVED', NOW(), NOW()),

-- Jazz Songs
('Blue Velvet Night', 'The Jazz Quartet', NULL, (SELECT id FROM genre WHERE name = 'Jazz' LIMIT 1), 312, '/uploads/2024/02/blue-velvet.mp3', 'mp3', 7440000, 'https://picsum.photos/seed/music8/300/300', 'Smooth jazz ballad', NULL, '2024-02-28', TRUE, 1567, 234, 1, 'APPROVED', NOW(), NOW()),
('Midnight Saxophone', 'Miles Harper', NULL, (SELECT id FROM genre WHERE name = 'Jazz' LIMIT 1), 245, '/uploads/2024/05/midnight-sax.mp3', 'mp3', 5840000, 'https://picsum.photos/seed/music9/300/300', 'Soulful saxophone performance', NULL, '2024-05-12', TRUE, 2123, 345, 2, 'APPROVED', NOW(), NOW()),

-- Classical Songs
('Moonlight Sonata Reimagined', 'The Modern Orchestra', 4, (SELECT id FROM genre WHERE name = 'Classical' LIMIT 1), 423, '/uploads/2024/01/moonlight-modern.mp3', 'mp3', 10080000, 'https://picsum.photos/seed/music10/300/300', 'Modern interpretation of Beethoven classic', NULL, '2024-01-05', TRUE, 2890, 456, 1, 'APPROVED', NOW(), NOW()),
('Spring Symphony', 'The Modern Orchestra', 4, (SELECT id FROM genre WHERE name = 'Classical' LIMIT 1), 378, '/uploads/2024/03/spring-symphony.mp3', 'mp3', 9010000, 'https://picsum.photos/seed/music11/300/300', 'Vibrant spring-themed orchestral piece', NULL, '2024-03-21', TRUE, 1678, 289, 1, 'APPROVED', NOW(), NOW()),

-- Acoustic Songs
('Coffee Shop Serenade', 'Sarah Mitchell', 2, (SELECT id FROM genre WHERE name = 'Folk' LIMIT 1), 198, '/uploads/2024/06/coffee-serenade.mp3', 'mp3', 4720000, 'https://picsum.photos/seed/music12/300/300', 'Intimate acoustic guitar and vocals', 'Verse 1: In this quiet corner...\nChorus: Coffee and melodies...', '2024-06-20', TRUE, 3421, 534, 2, 'APPROVED', NOW(), NOW()),
('Mountain Echo', 'Sarah Mitchell', 2, (SELECT id FROM genre WHERE name = 'Folk' LIMIT 1), 234, '/uploads/2024/06/mountain-echo.mp3', 'mp3', 5580000, 'https://picsum.photos/seed/music13/300/300', 'Folk song inspired by nature', NULL, '2024-06-22', TRUE, 2567, 401, 2, 'APPROVED', NOW(), NOW()),

-- Electronic Songs
('Digital Dreams', 'Electro Masters', NULL, (SELECT id FROM genre WHERE name = 'Electronic' LIMIT 1), 289, '/uploads/2024/04/digital-dreams.mp3', 'mp3', 6890000, 'https://picsum.photos/seed/music14/300/300', 'Progressive electronic track', NULL, '2024-04-18', TRUE, 4123, 623, 1, 'APPROVED', NOW(), NOW()),
('Pulse', 'Techno Collective', NULL, (SELECT id FROM genre WHERE name = 'Electronic' LIMIT 1), 267, '/uploads/2024/05/pulse.mp3', 'mp3', 6360000, 'https://picsum.photos/seed/music15/300/300', 'High-energy techno banger', NULL, '2024-05-08', TRUE, 3890, 567, 1, 'APPROVED', NOW(), NOW()),

-- More diverse tracks
('Rainy Day Blues', 'The Blues Brothers Revival', NULL, (SELECT id FROM genre WHERE name = 'Blues' LIMIT 1), 256, '/uploads/2024/03/rainy-blues.mp3', 'mp3', 6100000, 'https://picsum.photos/seed/music16/300/300', 'Soulful blues track', NULL, '2024-03-10', TRUE, 1890, 278, 2, 'APPROVED', NOW(), NOW()),
('Reggae Sunrise', 'Island Vibes', NULL, (SELECT id FROM genre WHERE name = 'Reggae' LIMIT 1), 243, '/uploads/2024/07/reggae-sunrise.mp3', 'mp3', 5790000, 'https://picsum.photos/seed/music17/300/300', 'Chill reggae vibes', NULL, '2024-07-15', TRUE, 2456, 389, 1, 'APPROVED', NOW(), NOW()),
('Country Roads Home', 'Nashville Stars', NULL, (SELECT id FROM genre WHERE name = 'Country' LIMIT 1), 234, '/uploads/2024/05/country-roads.mp3', 'mp3', 5580000, 'https://picsum.photos/seed/music18/300/300', 'Classic country ballad', 'Verse 1: Down that dusty road...\nChorus: Taking me back home...', '2024-05-20', TRUE, 2123, 345, 2, 'APPROVED', NOW(), NOW()),
('Metal Thunder', 'Iron Warriors', NULL, (SELECT id FROM genre WHERE name = 'Metal' LIMIT 1), 312, '/uploads/2024/02/metal-thunder.mp3', 'mp3', 7440000, 'https://picsum.photos/seed/music19/300/300', 'Heavy metal masterpiece', NULL, '2024-02-14', TRUE, 3234, 489, 1, 'APPROVED', NOW(), NOW()),
('Disco Fever', 'Funky Groove', NULL, (SELECT id FROM genre WHERE name = 'Disco' LIMIT 1), 198, '/uploads/2024/06/disco-fever.mp3', 'mp3', 4720000, 'https://picsum.photos/seed/music20/300/300', 'Classic disco groove', NULL, '2024-06-05', TRUE, 4567, 701, 1, 'APPROVED', NOW(), NOW());

-- ========================================
-- Insert Test Playlists
-- ========================================
INSERT INTO playlist (name, description, cover_image, is_public, creator_id, play_count, like_count, created_at, updated_at) VALUES
('Chill Vibes', 'Perfect playlist for relaxing and unwinding after a long day', 'https://picsum.photos/seed/playlist1/400/400', TRUE, 1, 856, 123, NOW(), NOW()),
('Workout Motivation', 'High-energy tracks to power through your workout', 'https://picsum.photos/seed/playlist2/400/400', TRUE, 1, 1234, 234, NOW(), NOW()),
('Study Focus', 'Instrumental and ambient music for concentration', 'https://picsum.photos/seed/playlist3/400/400', TRUE, 2, 2341, 456, NOW(), NOW()),
('Late Night Drive', 'Smooth tracks for those midnight cruises', 'https://picsum.photos/seed/playlist4/400/400', TRUE, 1, 678, 98, NOW(), NOW()),
('Summer Party 2024', 'Ultimate summer party playlist with the hottest tracks', 'https://picsum.photos/seed/playlist5/400/400', TRUE, 2, 3456, 678, NOW(), NOW());

-- ========================================
-- Add Music to Playlists
-- ========================================
-- Chill Vibes Playlist
INSERT INTO playlist_music (playlist_id, music_id, position, added_at) VALUES
((SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), (SELECT id FROM music WHERE title = 'Midnight Dreams' LIMIT 1), 1, NOW()),
((SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), (SELECT id FROM music WHERE title = 'Blue Velvet Night' LIMIT 1), 2, NOW()),
((SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), (SELECT id FROM music WHERE title = 'Coffee Shop Serenade' LIMIT 1), 3, NOW()),
((SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), (SELECT id FROM music WHERE title = 'Reggae Sunrise' LIMIT 1), 4, NOW()),
((SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), (SELECT id FROM music WHERE title = 'Mountain Echo' LIMIT 1), 5, NOW());

-- Workout Motivation Playlist
INSERT INTO playlist_music (playlist_id, music_id, position, added_at) VALUES
((SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), (SELECT id FROM music WHERE title = 'Thunder Road' LIMIT 1), 1, NOW()),
((SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), (SELECT id FROM music WHERE title = 'Electric Storm' LIMIT 1), 2, NOW()),
((SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), (SELECT id FROM music WHERE title = 'Metal Thunder' LIMIT 1), 3, NOW()),
((SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), (SELECT id FROM music WHERE title = 'Beat Drop' LIMIT 1), 4, NOW()),
((SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), (SELECT id FROM music WHERE title = 'Pulse' LIMIT 1), 5, NOW());

-- Study Focus Playlist
INSERT INTO playlist_music (playlist_id, music_id, position, added_at) VALUES
((SELECT id FROM playlist WHERE name = 'Study Focus' LIMIT 1), (SELECT id FROM music WHERE title = 'Moonlight Sonata Reimagined' LIMIT 1), 1, NOW()),
((SELECT id FROM playlist WHERE name = 'Study Focus' LIMIT 1), (SELECT id FROM music WHERE title = 'Spring Symphony' LIMIT 1), 2, NOW()),
((SELECT id FROM playlist WHERE name = 'Study Focus' LIMIT 1), (SELECT id FROM music WHERE title = 'Blue Velvet Night' LIMIT 1), 3, NOW()),
((SELECT id FROM playlist WHERE name = 'Study Focus' LIMIT 1), (SELECT id FROM music WHERE title = 'Midnight Saxophone' LIMIT 1), 4, NOW());

-- Late Night Drive Playlist
INSERT INTO playlist_music (playlist_id, music_id, position, added_at) VALUES
((SELECT id FROM playlist WHERE name = 'Late Night Drive' LIMIT 1), (SELECT id FROM music WHERE title = 'Neon Lights' LIMIT 1), 1, NOW()),
((SELECT id FROM playlist WHERE name = 'Late Night Drive' LIMIT 1), (SELECT id FROM music WHERE title = 'City Streets' LIMIT 1), 2, NOW()),
((SELECT id FROM playlist WHERE name = 'Late Night Drive' LIMIT 1), (SELECT id FROM music WHERE title = 'Digital Dreams' LIMIT 1), 3, NOW()),
((SELECT id FROM playlist WHERE name = 'Late Night Drive' LIMIT 1), (SELECT id FROM music WHERE title = 'Midnight Dreams' LIMIT 1), 4, NOW());

-- Summer Party 2024 Playlist
INSERT INTO playlist_music (playlist_id, music_id, position, added_at) VALUES
((SELECT id FROM playlist WHERE name = 'Summer Party 2024' LIMIT 1), (SELECT id FROM music WHERE title = 'Summer Love' LIMIT 1), 1, NOW()),
((SELECT id FROM playlist WHERE name = 'Summer Party 2024' LIMIT 1), (SELECT id FROM music WHERE title = 'Disco Fever' LIMIT 1), 2, NOW()),
((SELECT id FROM playlist WHERE name = 'Summer Party 2024' LIMIT 1), (SELECT id FROM music WHERE title = 'Beat Drop' LIMIT 1), 3, NOW()),
((SELECT id FROM playlist WHERE name = 'Summer Party 2024' LIMIT 1), (SELECT id FROM music WHERE title = 'Reggae Sunrise' LIMIT 1), 4, NOW()),
((SELECT id FROM playlist WHERE name = 'Summer Party 2024' LIMIT 1), (SELECT id FROM music WHERE title = 'Pulse' LIMIT 1), 5, NOW());

-- ========================================
-- Insert Sample Comments
-- ========================================
-- Comments on Music
INSERT INTO comment (content, target_type, target_id, user_id, like_count, reply_count, created_at, updated_at) VALUES
('This track is absolutely amazing! The production quality is top-notch.', 'MUSIC', (SELECT id FROM music WHERE title = 'Midnight Dreams' LIMIT 1), 2, 15, 2, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
('Love the synthwave vibes! Takes me back to the 80s.', 'MUSIC', (SELECT id FROM music WHERE title = 'Neon Lights' LIMIT 1), 2, 23, 1, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
('Perfect summer anthem! Playing this all season long.', 'MUSIC', (SELECT id FROM music WHERE title = 'Summer Love' LIMIT 1), 1, 34, 0, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
('The guitar solo gives me chills every time!', 'MUSIC', (SELECT id FROM music WHERE title = 'Thunder Road' LIMIT 1), 2, 28, 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
('Best hip-hop track I''ve heard this year.', 'MUSIC', (SELECT id FROM music WHERE title = 'City Streets' LIMIT 1), 1, 19, 3, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('The saxophone in this is pure magic!', 'MUSIC', (SELECT id FROM music WHERE title = 'Midnight Saxophone' LIMIT 1), 2, 12, 0, NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours');

-- Replies to Comments
INSERT INTO comment (content, target_type, target_id, parent_comment_id, user_id, like_count, reply_count, created_at, updated_at) VALUES
('I agree! The mixing is incredible.', 'MUSIC', (SELECT id FROM music WHERE title = 'Midnight Dreams' LIMIT 1), (SELECT id FROM comment WHERE content LIKE 'This track is absolutely%' LIMIT 1), 1, 8, 0, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
('Can''t wait for more tracks like this!', 'MUSIC', (SELECT id FROM music WHERE title = 'Midnight Dreams' LIMIT 1), (SELECT id FROM comment WHERE content LIKE 'This track is absolutely%' LIMIT 1), 2, 5, 0, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days');

-- Comments on Playlists
INSERT INTO comment (content, target_type, target_id, user_id, like_count, reply_count, created_at, updated_at) VALUES
('This playlist is perfect for my morning routine!', 'PLAYLIST', (SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), 2, 45, 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
('Great selection of tracks! Added to my library.', 'PLAYLIST', (SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), 1, 32, 0, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('Helped me ace my exams! Thanks for the focus vibes.', 'PLAYLIST', (SELECT id FROM playlist WHERE name = 'Study Focus' LIMIT 1), 2, 28, 2, NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours');

-- ========================================
-- Insert Sample Likes
-- ========================================
-- Likes on Music
INSERT INTO like_record (target_type, target_id, user_id, created_at) VALUES
('MUSIC', (SELECT id FROM music WHERE title = 'Midnight Dreams' LIMIT 1), 2, NOW() - INTERVAL '5 days'),
('MUSIC', (SELECT id FROM music WHERE title = 'Neon Lights' LIMIT 1), 2, NOW() - INTERVAL '4 days'),
('MUSIC', (SELECT id FROM music WHERE title = 'Summer Love' LIMIT 1), 1, NOW() - INTERVAL '3 days'),
('MUSIC', (SELECT id FROM music WHERE title = 'Thunder Road' LIMIT 1), 2, NOW() - INTERVAL '2 days'),
('MUSIC', (SELECT id FROM music WHERE title = 'City Streets' LIMIT 1), 1, NOW() - INTERVAL '1 day'),
('MUSIC', (SELECT id FROM music WHERE title = 'Digital Dreams' LIMIT 1), 2, NOW() - INTERVAL '12 hours'),
('MUSIC', (SELECT id FROM music WHERE title = 'Disco Fever' LIMIT 1), 1, NOW() - INTERVAL '6 hours');

-- Likes on Playlists
INSERT INTO like_record (target_type, target_id, user_id, created_at) VALUES
('PLAYLIST', (SELECT id FROM playlist WHERE name = 'Chill Vibes' LIMIT 1), 2, NOW() - INTERVAL '5 days'),
('PLAYLIST', (SELECT id FROM playlist WHERE name = 'Workout Motivation' LIMIT 1), 1, NOW() - INTERVAL '4 days'),
('PLAYLIST', (SELECT id FROM playlist WHERE name = 'Study Focus' LIMIT 1), 2, NOW() - INTERVAL '3 days'),
('PLAYLIST', (SELECT id FROM playlist WHERE name = 'Summer Party 2024' LIMIT 1), 1, NOW() - INTERVAL '2 days');

-- Likes on Comments
INSERT INTO like_record (target_type, target_id, user_id, created_at) VALUES
('COMMENT', (SELECT id FROM comment WHERE content LIKE 'This track is absolutely%' LIMIT 1), 1, NOW() - INTERVAL '4 days'),
('COMMENT', (SELECT id FROM comment WHERE content LIKE 'Love the synthwave%' LIMIT 1), 1, NOW() - INTERVAL '3 days'),
('COMMENT', (SELECT id FROM comment WHERE content LIKE 'Perfect summer anthem%' LIMIT 1), 2, NOW() - INTERVAL '2 days');

-- ========================================
-- Insert Sample Follows (if users exist)
-- ========================================
INSERT INTO follow (follower_id, following_id, created_at) VALUES
(2, 1, NOW() - INTERVAL '10 days'),
(1, 2, NOW() - INTERVAL '8 days');

-- ========================================
-- Update Statistics
-- ========================================
-- Update music counts in playlists
UPDATE playlist p SET music_count = (
    SELECT COUNT(*) FROM playlist_music pm WHERE pm.playlist_id = p.id
);

-- Note: play_count and like_count are already set in the INSERT statements above
-- In a real application, these would be updated by triggers or application logic

-- ========================================
-- End of Test Data
-- ========================================
