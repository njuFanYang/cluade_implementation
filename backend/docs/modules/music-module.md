# 🎵 Music Module Documentation

**Module:** Music Content Management
**Version:** 1.0.0
**Date:** 2026-02-20
**Author:** MusicShare Team

---

## 📋 Module Overview

The Music Module is the core of the MusicShare platform, providing comprehensive functionality for music upload, storage, playback, and management. It includes support for genres, albums, playlists, and file streaming.

### Key Features

- **Music Upload**: Support for multiple audio formats (MP3, FLAC, AAC, OGG, WAV)
- **Metadata Extraction**: Automatic extraction of title, artist, duration, and other metadata
- **File Storage**: Local file system storage with organized directory structure
- **Streaming Playback**: HTTP streaming with Range request support
- **Genre Management**: 50+ pre-defined music genres
- **Album Management**: Organize music into albums
- **Playlist Management**: Create, manage, and share playlists
- **Search & Discovery**: Full-text search, popular music, recent uploads
- **Permission Control**: Role-based and ownership-based access control

---

## 🗄️ Database Schema

### 1. genres Table (Music Genres)

```sql
CREATE TABLE genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    name_zh VARCHAR(50),
    description VARCHAR(500),
    icon VARCHAR(255),
    music_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_music_count (music_count)
);
```

**Initial Data**: 50 genres including Pop, Rock, Jazz, Classical, Electronic, Hip Hop, etc.

### 2. albums Table (Music Albums)

```sql
CREATE TABLE albums (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(100) NOT NULL,
    cover_image VARCHAR(500),
    description TEXT,
    release_date DATE,
    creator_id BIGINT NOT NULL,
    music_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_artist (artist),
    INDEX idx_creator (creator_id),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 3. music Table (Main Music Table)

```sql
CREATE TABLE music (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(100) NOT NULL,
    album_id BIGINT,
    genre_id BIGINT NOT NULL,
    duration INT NOT NULL,
    file_path VARCHAR(500) NOT NULL UNIQUE,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_format VARCHAR(10) NOT NULL,
    cover_image VARCHAR(500),
    description TEXT,
    lyrics TEXT,
    release_date DATE,
    uploader_id BIGINT NOT NULL,
    play_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    download_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
);
```

### 4. playlists Table (User Playlists)

```sql
CREATE TABLE playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    cover_image VARCHAR(500),
    creator_id BIGINT NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    music_count INT NOT NULL DEFAULT 0,
    play_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_creator (creator_id),
    INDEX idx_public (is_public),
    INDEX idx_created (created_at),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 5. playlist_music Table (Playlist-Music Association)

```sql
CREATE TABLE playlist_music (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,
    position INT NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_playlist_music (playlist_id, music_id),
    INDEX idx_playlist (playlist_id),
    INDEX idx_music (music_id),
    INDEX idx_position (playlist_id, position),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (music_id) REFERENCES music(id) ON DELETE CASCADE
);
```

---

## 🏗️ Architecture

### Entity Layer

**Core Entities:**
- `Music`: Main music entity with 30+ fields
- `Genre`: Music genre/category
- `Album`: Music album
- `Playlist`: User-created playlist
- `PlaylistMusic`: Association between playlist and music
- `MusicStatus`: Enum for music approval status (PENDING, APPROVED, REJECTED)

**Entity Relationships:**
- Music → Genre (Many-to-One)
- Music → Album (Many-to-One, optional)
- Music → User/Uploader (Many-to-One)
- Playlist → User/Creator (Many-to-One)
- PlaylistMusic → Playlist (Many-to-One)
- PlaylistMusic → Music (Many-to-One)

### Repository Layer

**Repositories:**
- `MusicRepository`: 15+ query methods including search, popular, recent
- `GenreRepository`: Genre queries with sorting
- `AlbumRepository`: Album queries and search
- `PlaylistRepository`: Playlist queries with public/private filtering
- `PlaylistMusicRepository`: Association management with ordering

### Service Layer

**Services:**
1. **FileStorageService**: File upload, storage, streaming, metadata extraction
2. **MusicService**: Music CRUD, upload, search, playback
3. **PlaylistService**: Playlist CRUD, music management, reordering
4. **GenreService**: Genre queries

### Controller Layer

**Controllers:**
1. **MusicController**: 13 API endpoints for music management
2. **PlaylistController**: 11 API endpoints for playlist management
3. **GenreController**: 3 API endpoints for genre queries

---

## 🔌 API Endpoints

### Music APIs

**Upload Music**
```
POST /api/music/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}
Role: MUSICIAN, ADMIN

Request:
- title: string (required)
- artist: string (required)
- genreId: long (required)
- albumId: long (optional)
- description: string (optional)
- lyrics: string (optional)
- releaseDate: date (optional)
- isPublic: boolean (default: true)
- file: multipart file (required)

Response: MusicDetailResponse
```

**Get Music Detail**
```
GET /api/music/{id}

Response: MusicDetailResponse
```

**Update Music**
```
PUT /api/music/{id}
Authorization: Bearer {token}
Permission: Owner or Admin

Request: UpdateMusicRequest
Response: MusicResponse
```

**Delete Music**
```
DELETE /api/music/{id}
Authorization: Bearer {token}
Permission: Owner or Admin

Response: Success message
```

**Get My Music**
```
GET /api/music/my?page=0&size=20&sort=createdAt,desc
Authorization: Bearer {token}

Response: Page<MusicResponse>
```

**Search Music**
```
GET /api/music/search?keyword={keyword}&page=0&size=20

Response: Page<MusicResponse>
```

**Get Popular Music**
```
GET /api/music/popular?page=0&size=20

Response: Page<MusicResponse>
```

**Get Recent Music**
```
GET /api/music/recent?page=0&size=20

Response: Page<MusicResponse>
```

**Get Music by Genre**
```
GET /api/music/genre/{genreId}?page=0&size=20

Response: Page<MusicResponse>
```

**Get All Public Music**
```
GET /api/music?page=0&size=20&sort=createdAt,desc

Response: Page<MusicResponse>
```

**Stream Music**
```
GET /api/music/{id}/stream

Response: Audio stream (supports HTTP Range requests)
Content-Type: audio/mpeg, audio/flac, etc.
```

**Record Play**
```
POST /api/music/{id}/play

Response: Success message
```

### Playlist APIs

**Create Playlist**
```
POST /api/playlists
Authorization: Bearer {token}

Request:
- name: string (required)
- description: string (optional)
- coverImage: string (optional)
- isPublic: boolean (default: true)

Response: PlaylistResponse
```

**Get Playlist Detail**
```
GET /api/playlists/{id}

Response: PlaylistDetailResponse (includes music list)
```

**Update Playlist**
```
PUT /api/playlists/{id}
Authorization: Bearer {token}
Permission: Owner

Request: UpdatePlaylistRequest
Response: PlaylistResponse
```

**Delete Playlist**
```
DELETE /api/playlists/{id}
Authorization: Bearer {token}
Permission: Owner

Response: Success message
```

**Get My Playlists**
```
GET /api/playlists/my?page=0&size=20
Authorization: Bearer {token}

Response: Page<PlaylistResponse>
```

**Get Public Playlists**
```
GET /api/playlists/public?page=0&size=20

Response: Page<PlaylistResponse>
```

**Search Playlists**
```
GET /api/playlists/search?keyword={keyword}&page=0&size=20

Response: Page<PlaylistResponse>
```

**Get Popular Playlists**
```
GET /api/playlists/popular?page=0&size=20

Response: Page<PlaylistResponse>
```

**Add Music to Playlist**
```
POST /api/playlists/{playlistId}/music/{musicId}
Authorization: Bearer {token}
Permission: Owner

Response: Success message
```

**Remove Music from Playlist**
```
DELETE /api/playlists/{playlistId}/music/{musicId}
Authorization: Bearer {token}
Permission: Owner

Response: Success message
```

**Reorder Playlist Music**
```
PUT /api/playlists/{playlistId}/reorder
Authorization: Bearer {token}
Permission: Owner

Request: [musicId1, musicId2, ...]
Response: Success message
```

### Genre APIs

**Get All Genres**
```
GET /api/genres

Response: List<GenreResponse>
```

**Get Popular Genres**
```
GET /api/genres/popular

Response: List<GenreResponse>
```

**Get Genre by ID**
```
GET /api/genres/{id}

Response: GenreResponse
```

---

## 📦 File Storage

### Directory Structure

```
uploads/
├── music/              # Music files
│   ├── 2026/
│   │   └── 02/
│   │       └── {uuid}_{timestamp}.mp3
└── covers/             # Cover images
    ├── 2026/
        └── 02/
            └── {uuid}_{timestamp}.jpg
```

### Upload Process

1. Validate file type (mp3, flac, aac, ogg, wav)
2. Validate file size (max 100MB)
3. Generate unique filename: UUID + timestamp + extension
4. Create year/month subdirectory
5. Save file to disk
6. Extract metadata using JAudiotagger
7. Save metadata to database
8. Return file path

### Streaming

- **Protocol**: HTTP/1.1
- **Range Support**: Yes (for seeking)
- **Content-Type**: Auto-detected based on file extension
- **Caching**: Controlled via HTTP headers
- **Security**: Permission check before streaming

---

## 🔒 Security & Permissions

### Role-Based Access Control

**Upload Music:**
- Required Role: `ROLE_MUSICIAN` or `ROLE_ADMIN`

**Modify Music:**
- Owner: Can update/delete own music
- Admin: Can update/delete any music

**Access Music:**
- Public Approved: Anyone can view/stream
- Private: Only owner can access
- Pending: Only owner can access

**Playlist Management:**
- Create: Any authenticated user
- Modify: Only owner
- View Public: Anyone
- View Private: Only owner

### Permission Checks

```java
// Check if user can access music
public boolean isAccessibleBy(Long userId) {
    if (isPublic && isApproved()) return true;
    if (userId != null && isOwnedBy(userId)) return true;
    return false;
}

// Check if user owns music
public boolean isOwnedBy(Long userId) {
    return this.uploaderId.equals(userId);
}
```

---

## 🛠️ Configuration

### File Storage Configuration

```yaml
file:
  upload-dir: ./uploads
  music-dir: ./uploads/music
  cover-dir: ./uploads/covers
  allowed-music-types: mp3,flac,aac,ogg,wav
  allowed-image-types: jpg,jpeg,png,gif,webp
  max-music-size: 104857600  # 100MB
  max-image-size: 5242880    # 5MB
```

### Maven Dependencies

```xml
<!-- Audio metadata extraction -->
<dependency>
    <groupId>net.jthink</groupId>
    <artifactId>jaudiotagger</artifactId>
    <version>3.0.1</version>
</dependency>
```

---

## ⚠️ Error Codes

### Music Module (2000-2999)

- `2001`: MUSIC_NOT_FOUND - Music not found
- `2002`: MUSIC_UPLOAD_FAILED - Music upload failed
- `2003`: INVALID_MUSIC_FORMAT - Invalid music file format
- `2004`: MUSIC_FILE_TOO_LARGE - Music file size exceeds limit
- `2005`: MUSIC_ALREADY_EXISTS - Music already exists
- `2006`: MUSIC_NOT_APPROVED - Music not approved yet
- `2007`: MUSIC_PROCESSING - Music is being processed
- `2008`: MUSIC_PERMISSION_DENIED - Permission denied to access this music
- `2009`: ALBUM_NOT_FOUND - Album not found
- `2010`: GENRE_NOT_FOUND - Genre not found
- `2011`: MUSIC_METADATA_EXTRACTION_FAILED - Failed to extract music metadata

### Playlist Module (3000-3999)

- `3001`: PLAYLIST_NOT_FOUND - Playlist not found
- `3002`: PLAYLIST_NAME_EXISTS - Playlist name already exists
- `3003`: MUSIC_ALREADY_IN_PLAYLIST - Music already in playlist
- `3004`: MUSIC_NOT_IN_PLAYLIST - Music not in playlist
- `3005`: CANNOT_MODIFY_PLAYLIST - Cannot modify this playlist
- `3006`: PLAYLIST_PERMISSION_DENIED - Permission denied to access this playlist
- `3007`: PLAYLIST_MUSIC_LIMIT_EXCEEDED - Playlist has reached maximum music limit

### File Module (6000-6999)

- `6001`: FILE_UPLOAD_FAILED - File upload failed
- `6002`: FILE_NOT_FOUND - File not found
- `6003`: FILE_TOO_LARGE - File size exceeds limit
- `6004`: INVALID_FILE_TYPE - Invalid file type
- `6005`: FILE_READ_ERROR - File read error
- `6006`: FILE_DELETE_FAILED - File delete failed

---

## 🧪 Testing

### Test Scenarios

**Music Upload:**
- ✅ Upload valid music file
- ✅ Upload invalid file type → Error 6004
- ✅ Upload oversized file → Error 6003
- ✅ Extract metadata successfully
- ✅ Handle metadata extraction failure gracefully

**Music Access:**
- ✅ Access public approved music
- ✅ Access own private music
- ✅ Access other's private music → Error 2008
- ✅ Stream music with Range request

**Playlist Management:**
- ✅ Create playlist
- ✅ Add music to playlist
- ✅ Add duplicate music → Error 3003
- ✅ Remove music from playlist
- ✅ Reorder playlist music
- ✅ Access private playlist → Permission check

---

## 📊 Performance Considerations

### Database Optimization

- **Indexes**: Created on frequently queried columns (title, artist, genre, created_at, play_count)
- **Eager Loading**: Use `JOIN FETCH` for related entities to avoid N+1 queries
- **Pagination**: All list queries support pagination to limit result size

### File Operations

- **Streaming**: Use Spring Resource for efficient streaming
- **Buffering**: File operations use buffered streams
- **Cleanup**: Temporary files cleaned up after metadata extraction

### Caching Opportunities (Future)

- Genre list (rarely changes)
- Popular music (can be cached for short period)
- Music metadata (after first load)

---

## 🚀 Future Enhancements

### Short-term

- [ ] Cover image upload and processing
- [ ] Batch music upload
- [ ] Music recommendations based on listening history
- [ ] Playlist collaborative editing

### Medium-term

- [ ] Cloud storage integration (AWS S3, Aliyun OSS)
- [ ] Audio format conversion
- [ ] Playlist import/export (M3U, PLS formats)
- [ ] Music sharing and embedding

### Long-term

- [ ] AI-powered music classification
- [ ] Automatic playlist generation
- [ ] Music visualization
- [ ] Live streaming support

---

## 📚 Related Documentation

- [User Module Documentation](./user-module.md)
- [Database Design](../DATABASE_DESIGN.md)
- [API Design](../API_DESIGN.md)
- [Project Status](../../PROJECT_STATUS.md)

---

**Last Updated:** 2026-02-20
**Version:** 1.0.0
**Status:** ✅ Implemented
