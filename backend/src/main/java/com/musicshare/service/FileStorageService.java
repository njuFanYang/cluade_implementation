package com.musicshare.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * File Storage Service Interface
 *
 * <p>Defines methods for file storage operations including upload, delete,
 * retrieval, and validation for music and image files.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public interface FileStorageService {

    /**
     * Store a file to the specified subdirectory
     *
     * @param file        File to store
     * @param subdirectory Subdirectory (e.g., "music", "covers", "avatars")
     * @return Stored file path (relative)
     */
    String storeFile(MultipartFile file, String subdirectory);

    /**
     * Delete a file by its path
     *
     * @param filePath File path to delete (relative)
     * @return true if deletion was successful
     */
    boolean deleteFile(String filePath);

    /**
     * Load file as a resource for streaming
     *
     * @param filePath File path to load (relative)
     * @return Resource for streaming
     */
    Resource loadFileAsResource(String filePath);

    /**
     * Validate music file format and size
     *
     * @param file Music file to validate
     * @throws com.musicshare.exception.BusinessException if validation fails
     */
    void validateMusicFile(MultipartFile file);

    /**
     * Validate image file format and size
     *
     * @param file Image file to validate
     * @throws com.musicshare.exception.BusinessException if validation fails
     */
    void validateImageFile(MultipartFile file);

    /**
     * Extract metadata from music file
     *
     * @param file Music file
     * @return Map containing metadata (title, artist, duration, etc.)
     */
    Map<String, Object> extractMusicMetadata(MultipartFile file);

    /**
     * Generate a unique file name with timestamp and UUID
     *
     * @param originalFilename Original file name
     * @return Unique file name
     */
    String generateUniqueFileName(String originalFilename);

    /**
     * Get file extension from filename
     *
     * @param filename File name
     * @return File extension (without dot)
     */
    String getFileExtension(String filename);

    /**
     * Check if file path exists
     *
     * @param filePath File path to check (relative)
     * @return true if file exists
     */
    boolean fileExists(String filePath);

    /**
     * Get file size in bytes
     *
     * @param filePath File path (relative)
     * @return File size in bytes
     */
    long getFileSize(String filePath);
}
