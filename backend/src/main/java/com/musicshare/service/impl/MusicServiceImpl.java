package com.musicshare.service.impl;

import com.musicshare.dto.request.UpdateMusicRequest;
import com.musicshare.dto.request.UploadMusicRequest;
import com.musicshare.dto.response.MusicDetailResponse;
import com.musicshare.dto.response.MusicResponse;
import com.musicshare.entity.*;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.AlbumRepository;
import com.musicshare.repository.GenreRepository;
import com.musicshare.repository.MusicRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.service.FileStorageService;
import com.musicshare.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Music Service Implementation
 *
 * <p>Implements business logic for music management operations including
 * upload, CRUD operations, search, and streaming.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;
    private final GenreRepository genreRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public MusicDetailResponse uploadMusic(UploadMusicRequest request, MultipartFile file, Long uploaderId) {
        log.info("Uploading music: {} by {} for user ID: {}", request.getTitle(), request.getArtist(), uploaderId);

        // Validate music file
        fileStorageService.validateMusicFile(file);

        // Validate genre exists
        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GENRE_NOT_FOUND));

        // Validate album if provided
        Album album = null;
        if (request.getAlbumId() != null) {
            album = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));
        }

        // Validate uploader exists
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        try {
            // Extract metadata from music file
            Map<String, Object> metadata = fileStorageService.extractMusicMetadata(file);

            // Store music file
            String filePath = fileStorageService.storeFile(file, "music");
            String fileExtension = fileStorageService.getFileExtension(file.getOriginalFilename());

            // Create Music entity
            Music music = new Music();
            music.setTitle(request.getTitle());
            music.setArtist(request.getArtist());
            music.setAlbumId(request.getAlbumId());
            music.setGenreId(request.getGenreId());

            // Use metadata duration if available, otherwise default to 0
            Integer duration = metadata.containsKey("duration") ?
                    (Integer) metadata.get("duration") : 0;
            music.setDuration(duration);

            music.setFilePath(filePath);
            music.setFileName(file.getOriginalFilename());
            music.setFileSize(file.getSize());
            music.setFileFormat(fileExtension);
            music.setDescription(request.getDescription());
            music.setLyrics(request.getLyrics());
            music.setReleaseDate(request.getReleaseDate());
            music.setUploaderId(uploaderId);
            music.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
            music.setStatus(MusicStatus.APPROVED); // Auto-approve for now

            // Save music entity
            music = musicRepository.save(music);

            // Update genre music count
            genre.incrementMusicCount();
            genreRepository.save(genre);

            // Update album music count if applicable
            if (album != null) {
                album.incrementMusicCount();
                albumRepository.save(album);
            }

            // Update user music count
            uploader.setMusicCount(uploader.getMusicCount() + 1);
            userRepository.save(uploader);

            log.info("Music uploaded successfully with ID: {}", music.getId());

            // Build file URL
            String fileUrl = "/api/music/" + music.getId() + "/stream";

            // Reload with relationships
            music = musicRepository.findByIdWithDetails(music.getId())
                    .orElse(music);

            return MusicDetailResponse.fromEntity(music, fileUrl);

        } catch (Exception e) {
            log.error("Failed to upload music", e);
            throw new BusinessException(ErrorCode.MUSIC_UPLOAD_FAILED,
                    "Failed to upload music: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MusicDetailResponse getMusicById(Long id, Long userId) {
        log.debug("Getting music by ID: {} for user: {}", id, userId);

        Music music = musicRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        // Check access permission
        if (!music.isAccessibleBy(userId)) {
            log.warn("User {} attempted to access restricted music {}", userId, id);
            throw new BusinessException(ErrorCode.MUSIC_PERMISSION_DENIED);
        }

        String fileUrl = "/api/music/" + music.getId() + "/stream";
        return MusicDetailResponse.fromEntity(music, fileUrl);
    }

    @Override
    @Transactional
    public MusicResponse updateMusic(Long id, UpdateMusicRequest request, Long userId) {
        log.info("Updating music ID: {} by user ID: {}", id, userId);

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        // Check permission: only owner can update
        if (!music.isOwnedBy(userId)) {
            log.warn("User {} attempted to update music {} without permission", userId, id);
            throw new BusinessException(ErrorCode.MUSIC_PERMISSION_DENIED);
        }

        // Update fields if provided
        if (StringUtils.hasText(request.getTitle())) {
            music.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getArtist())) {
            music.setArtist(request.getArtist());
        }
        if (request.getGenreId() != null) {
            // Validate new genre
            Genre newGenre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.GENRE_NOT_FOUND));

            // Update genre counts if genre changed
            if (!music.getGenreId().equals(request.getGenreId())) {
                Genre oldGenre = genreRepository.findById(music.getGenreId()).orElse(null);
                if (oldGenre != null) {
                    oldGenre.decrementMusicCount();
                    genreRepository.save(oldGenre);
                }
                newGenre.incrementMusicCount();
                genreRepository.save(newGenre);
            }

            music.setGenreId(request.getGenreId());
        }
        if (request.getAlbumId() != null) {
            // Validate new album
            albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));
            music.setAlbumId(request.getAlbumId());
        }
        if (StringUtils.hasText(request.getDescription())) {
            music.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getLyrics())) {
            music.setLyrics(request.getLyrics());
        }
        if (request.getReleaseDate() != null) {
            music.setReleaseDate(request.getReleaseDate());
        }
        if (request.getIsPublic() != null) {
            music.setIsPublic(request.getIsPublic());
        }
        if (StringUtils.hasText(request.getCoverImage())) {
            music.setCoverImage(request.getCoverImage());
        }

        music = musicRepository.save(music);

        log.info("Music updated successfully: {}", id);

        // Reload with relationships
        music = musicRepository.findByIdWithDetails(id).orElse(music);
        return MusicResponse.fromEntity(music);
    }

    @Override
    @Transactional
    public void deleteMusic(Long id, Long userId) {
        log.info("Deleting music ID: {} by user ID: {}", id, userId);

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        // Check permission: only owner can delete
        if (!music.isOwnedBy(userId)) {
            log.warn("User {} attempted to delete music {} without permission", userId, id);
            throw new BusinessException(ErrorCode.MUSIC_PERMISSION_DENIED);
        }

        // Delete music file
        try {
            fileStorageService.deleteFile(music.getFilePath());
        } catch (Exception e) {
            log.warn("Failed to delete music file: {}", music.getFilePath(), e);
        }

        // Update genre music count
        Genre genre = genreRepository.findById(music.getGenreId()).orElse(null);
        if (genre != null) {
            genre.decrementMusicCount();
            genreRepository.save(genre);
        }

        // Update album music count if applicable
        if (music.getAlbumId() != null) {
            Album album = albumRepository.findById(music.getAlbumId()).orElse(null);
            if (album != null) {
                album.decrementMusicCount();
                albumRepository.save(album);
            }
        }

        // Update user music count
        User uploader = userRepository.findById(music.getUploaderId()).orElse(null);
        if (uploader != null && uploader.getMusicCount() > 0) {
            uploader.setMusicCount(uploader.getMusicCount() - 1);
            userRepository.save(uploader);
        }

        // Delete music entity
        musicRepository.delete(music);

        log.info("Music deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> searchMusic(String keyword, Pageable pageable) {
        log.debug("Searching music with keyword: {}", keyword);
        return musicRepository.searchMusicPublic(keyword, pageable)
                .map(MusicResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getPopularMusic(Pageable pageable) {
        log.debug("Getting popular music");
        return musicRepository.findPopularMusic(pageable)
                .map(MusicResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getRecentMusic(Pageable pageable) {
        log.debug("Getting recent music");
        return musicRepository.findRecentMusic(pageable)
                .map(MusicResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getMusicByGenre(Long genreId, Pageable pageable) {
        log.debug("Getting music by genre ID: {}", genreId);

        // Validate genre exists
        genreRepository.findById(genreId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GENRE_NOT_FOUND));

        return musicRepository.findByGenreIdPublic(genreId, pageable)
                .map(MusicResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getMusicByUploader(Long uploaderId, Pageable pageable) {
        log.debug("Getting music by uploader ID: {}", uploaderId);

        // Validate uploader exists
        userRepository.findById(uploaderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return musicRepository.findByUploaderId(uploaderId, pageable)
                .map(MusicResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicResponse> getAllPublicMusic(Pageable pageable) {
        log.debug("Getting all public music");
        return musicRepository.findAllPublicApproved(pageable)
                .map(MusicResponse::fromEntity);
    }

    @Override
    @Transactional
    public void incrementPlayCount(Long id) {
        log.debug("Incrementing play count for music ID: {}", id);

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        music.incrementPlayCount();
        musicRepository.save(music);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource streamMusic(Long id, Long userId) {
        log.debug("Streaming music ID: {} for user: {}", id, userId);

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        // Check access permission
        if (!music.isAccessibleBy(userId)) {
            log.warn("User {} attempted to stream restricted music {}", userId, id);
            throw new BusinessException(ErrorCode.MUSIC_PERMISSION_DENIED);
        }

        return fileStorageService.loadFileAsResource(music.getFilePath());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessMusic(Long musicId, Long userId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        return music.isAccessibleBy(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOwner(Long musicId, Long userId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        return music.isOwnedBy(userId);
    }
}
