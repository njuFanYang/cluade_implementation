-- ===================================================================================
-- MusicShare Database Migration
-- Version: V4
-- Description: Insert initial music genres data
-- Author: MusicShare Team
-- Date: 2026-02-20
-- ===================================================================================

-- ----------------------------
-- Initial Genres Data
-- ----------------------------
INSERT INTO genres (name, name_zh, description, icon) VALUES
('Pop', '流行', 'Popular music characterized by catchy melodies and widespread appeal', '🎵'),
('Rock', '摇滚', 'Music genre characterized by electric guitars, strong rhythms, and powerful vocals', '🎸'),
('Jazz', '爵士', 'Music genre characterized by swing, blue notes, and improvisation', '🎺'),
('Classical', '古典', 'Art music rooted in Western traditions, spanning from medieval to contemporary', '🎻'),
('Electronic', '电子', 'Music produced using electronic instruments and digital technology', '🎹'),
('Hip Hop', '嘻哈', 'Music genre featuring rhythmic vocal delivery over backing beats', '🎤'),
('R&B', 'R&B/节奏布鲁斯', 'Rhythm and Blues music combining elements of jazz, gospel, and blues', '🎶'),
('Country', '乡村', 'Music genre originating from American folk and Western traditions', '🤠'),
('Folk', '民谣', 'Traditional music passed down through generations', '🪕'),
('Blues', '布鲁斯', 'Music genre characterized by expressive vocals and guitar', '🎸'),
('Metal', '金属', 'Music genre characterized by heavy distortion and aggressive rhythms', '⚡'),
('Reggae', '雷鬼', 'Music genre originating from Jamaica with offbeat rhythms', '🌴'),
('Soul', '灵魂', 'Music genre combining R&B, gospel, and blues influences', '💫'),
('Funk', '放克', 'Music genre with strong rhythmic groove and syncopated bass lines', '🕺'),
('Disco', '迪斯科', 'Dance music from the 1970s with steady four-on-the-floor beat', '💃'),
('House', '浩室', 'Electronic dance music with repetitive 4/4 beats', '🏠'),
('Techno', '科技舞曲', 'Electronic dance music with futuristic and mechanical sound', '🤖'),
('Trance', '迷幻舞曲', 'Electronic music genre with hypnotic rhythms and melodies', '✨'),
('Dubstep', '回响贝斯', 'Electronic music with heavy bass and syncopated rhythms', '🔊'),
('Indie', '独立', 'Independent music produced outside major commercial labels', '🎨'),
('Alternative', '另类', 'Music that differs from mainstream commercial music', '🌟'),
('Punk', '朋克', 'Rock music characterized by short songs and anti-establishment lyrics', '💥'),
('Grunge', '垃圾摇滚', 'Rock music combining punk and heavy metal elements', '🧩'),
('Ska', '斯卡', 'Music genre with offbeat rhythms and horn sections', '🎺'),
('Latin', '拉丁', 'Music from Latin America with diverse rhythms and styles', '💃'),
('Salsa', '萨尔萨', 'Latin dance music from Cuba and Puerto Rico', '🌶️'),
('Reggaeton', '雷鬼顿', 'Music genre combining reggae, hip hop, and Latin influences', '🔥'),
('Bossa Nova', '波萨诺瓦', 'Brazilian music style blending samba and jazz', '🇧🇷'),
('Samba', '桑巴', 'Brazilian music and dance style with African roots', '🎊'),
('Flamenco', '弗拉明戈', 'Spanish music characterized by guitar, vocals, and dance', '💃'),
('K-Pop', '韩国流行', 'Korean popular music with diverse styles', '🇰🇷'),
('J-Pop', '日本流行', 'Japanese popular music', '🇯🇵'),
('C-Pop', '华语流行', 'Chinese popular music', '🇨🇳'),
('Bollywood', '宝莱坞', 'Indian film music combining traditional and modern elements', '🇮🇳'),
('World', '世界音乐', 'Music from various cultures around the world', '🌍'),
('Ambient', '氛围', 'Music emphasizing tone and atmosphere over structure', '🌌'),
('Chillout', '放松', 'Relaxing electronic music with downtempo beats', '😌'),
('Lofi', 'Lo-Fi嘻哈', 'Low fidelity music with relaxed beats', '📻'),
('Soundtrack', '原声带', 'Music composed for films, TV shows, or video games', '🎬'),
('Musical', '音乐剧', 'Songs from theatrical performances', '🎭'),
('Gospel', '福音', 'Christian music with spiritual themes', '⛪'),
('Blues Rock', '蓝调摇滚', 'Rock music with blues influences', '🎸'),
('Progressive Rock', '前卫摇滚', 'Complex rock music with extended compositions', '🎼'),
('Psychedelic', '迷幻摇滚', 'Rock music with surreal and experimental elements', '🌀'),
('Hard Rock', '硬摇滚', 'Rock music with heavy guitar riffs and vocals', '⚡'),
('Soft Rock', '软摇滚', 'Melodic rock with smooth vocals', '🌊'),
('New Wave', '新浪潮', 'Rock music from the late 1970s with electronic elements', '🌊'),
('Post-Punk', '后朋克', 'Experimental rock following punk movement', '🎨'),
('Emo', '情绪摇滚', 'Rock music with emotional lyrics', '💔'),
('Screamo', '尖叫摇滚', 'Hardcore punk with screaming vocals', '😱')
ON DUPLICATE KEY UPDATE name=VALUES(name);
