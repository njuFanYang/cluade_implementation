package com.musicshare.service;

import com.musicshare.dto.request.UpdateMusicRequest;
import com.musicshare.dto.request.UploadMusicRequest;
import com.musicshare.dto.response.MusicDetailResponse;
import com.musicshare.dto.response.MusicResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Music Service Interface
 *
 * <p>Defines business logic operations for music management, including
 * upload, CRUD operations, search, and streaming.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public interface MusicService {

    /**
     * Upload a new music file
     *
     * @param request  Music metadata
     * @param file     Music file
     * @param uploaderId ID of the user uploading the music
     * @return MusicDetailResponse containing uploaded music information
     * @throws com.musicshare.exception.BusinessException if upload fails or validation fails
     */
    MusicDetailResponse uploadMusic(UploadMusicRequest request, MultipartFile file, Long uploaderId);

    /**
     * Get music by ID
     *
     * @param id     Music ID
     * @param userId Current user ID (null if anonymous)
     * @return MusicDetailResponse containing music details
     * @throws com.musicshare.exception.BusinessException if music not found or no permission
     */
    MusicDetailResponse getMusicById(Long id, Long userId);

    /**
     * Update music information
     *
     * @param id      Music ID
     * @param request Update request
     * @param userId  Current user ID
     * @return MusicResponse containing updated music information
     * @throws com.musicshare.exception.BusinessException if music not found or no permission
     */
    MusicResponse updateMusic(Long id, UpdateMusicRequest request, Long userId);

    /**
     * Delete music
     *
     * @param id     Music ID
     * @param userId Current user ID
     * @throws com.musicshare.exception.BusinessException if music not found or no permission
     */
    void deleteMusic(Long id, Long userId);

    /**
     * Search music by keyword
     *
     * @param keyword  Search keyword
     * @param pageable Pagination parameters
     * @return Page of music matching the keyword
     */
    Page<MusicResponse> searchMusic(String keyword, Pageable pageable);

    /**
     * Get popular music
     *
     * @param pageable Pagination parameters
     * @return Page of popular music
     */
    Page<MusicResponse> getPopularMusic(Pageable pageable);

    /**
     * Get recent music
     *
     * @param pageable Pagination parameters
     * @return Page of recent music
     */
    Page<MusicResponse> getRecentMusic(Pageable pageable);

    /**
     * Get music by genre
     *
     * @param genreId  Genre ID
     * @param pageable Pagination parameters
     * @return Page of music in the genre
     */
    Page<MusicResponse> getMusicByGenre(Long genreId, Pageable pageable);

    /**
     * Get music by uploader
     *
     * @param uploaderId Uploader user ID
     * @param pageable   Pagination parameters
     * @return Page of music uploaded by the user
     */
    Page<MusicResponse> getMusicByUploader(Long uploaderId, Pageable pageable);

    /**
     * Get all public music
     *
     * @param pageable Pagination parameters
     * @return Page of public music
     */
    Page<MusicResponse> getAllPublicMusic(Pageable pageable);

    /**
     * Increment play count
     *
     * @param id Music ID
     */
    void incrementPlayCount(Long id);

    /**
     * Stream music file
     *
     * @param id     Music ID
     * @param userId Current user ID (null if anonymous)
     * @return Resource for streaming
     * @throws com.musicshare.exception.BusinessException if music not found or no permission
     */
    Resource streamMusic(Long id, Long userId);

    /**
     * Check if user can access music
     *
     * @param musicId Music ID
     * @param userId  User ID (null if anonymous)
     * @return true if user can access
     */
    boolean canUserAccessMusic(Long musicId, Long userId);

    /**
     * Check if user owns music
     *
     * @param musicId Music ID
     * @param userId  User ID
     * @return true if user owns the music
     */
    boolean isUserOwner(Long musicId, Long userId);
}
