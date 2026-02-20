package com.musicshare.service.impl;

import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * File Storage Service Implementation
 *
 * <p>Implements file storage operations using local file system.
 * Supports music and image file upload, deletion, and streaming.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.music-dir}")
    private String musicDir;

    @Value("${file.cover-dir}")
    private String coverDir;

    @Value("${file.avatar-dir}")
    private String avatarDir;

    @Value("${file.allowed-music-types}")
    private String allowedMusicTypes;

    @Value("${file.allowed-image-types}")
    private String allowedImageTypes;

    @Value("${file.max-music-size}")
    private Long maxMusicSize;

    @Value("${file.max-image-size}")
    private Long maxImageSize;

    private Path uploadPath;
    private Set<String> allowedMusicExtensions;
    private Set<String> allowedImageExtensions;

    /**
     * Initialize storage directories and allowed file types
     */
    @PostConstruct
    public void init() {
        try {
            // Initialize upload path
            this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Create subdirectories
            Files.createDirectories(Paths.get(musicDir));
            Files.createDirectories(Paths.get(coverDir));
            Files.createDirectories(Paths.get(avatarDir));

            // Parse allowed file types
            this.allowedMusicExtensions = new HashSet<>(Arrays.asList(
                    allowedMusicTypes.toLowerCase().split(",")));
            this.allowedImageExtensions = new HashSet<>(Arrays.asList(
                    allowedImageTypes.toLowerCase().split(",")));

            log.info("File storage service initialized successfully");
            log.info("Upload directory: {}", uploadPath);
        } catch (IOException e) {
            log.error("Failed to initialize file storage service", e);
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subdirectory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "File is empty");
        }

        try {
            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename()));
            String uniqueFilename = generateUniqueFileName(originalFilename);

            // Create date-based subdirectory (YYYY/MM)
            LocalDate now = LocalDate.now();
            String dateSubdir = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));
            Path targetDir = Paths.get(subdirectory, dateSubdir);
            Files.createDirectories(targetDir);

            // Build target path
            Path targetPath = targetDir.resolve(uniqueFilename);

            // Copy file to target location
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path
            String relativePath = targetPath.toString().replace("\\", "/");
            log.info("File stored successfully: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED,
                    "Could not store file: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();

            // Security check: ensure path is within upload directory
            if (!path.startsWith(uploadPath)) {
                log.warn("Attempted to delete file outside upload directory: {}", filePath);
                return false;
            }

            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("File deleted successfully: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
            return deleted;

        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED,
                    "Could not delete file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();

            // Security check: ensure path is within upload directory
            if (!path.startsWith(uploadPath)) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND,
                        "File path is outside upload directory");
            }

            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND,
                        "File not found: " + filePath);
            }

        } catch (IOException e) {
            log.error("Failed to load file as resource: {}", filePath, e);
            throw new BusinessException(ErrorCode.FILE_READ_ERROR,
                    "Could not read file: " + e.getMessage());
        }
    }

    @Override
    public void validateMusicFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "File is empty");
        }

        // Check file size
        if (file.getSize() > maxMusicSize) {
            throw new BusinessException(ErrorCode.MUSIC_FILE_TOO_LARGE,
                    String.format("File size exceeds maximum limit of %d MB",
                            maxMusicSize / 1024 / 1024));
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
                    "Invalid file name");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!allowedMusicExtensions.contains(extension)) {
            throw new BusinessException(ErrorCode.INVALID_MUSIC_FORMAT,
                    "Unsupported music format. Allowed formats: " +
                    String.join(", ", allowedMusicExtensions));
        }

        log.debug("Music file validation passed: {}", filename);
    }

    @Override
    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "File is empty");
        }

        // Check file size
        if (file.getSize() > maxImageSize) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE,
                    String.format("Image size exceeds maximum limit of %d MB",
                            maxImageSize / 1024 / 1024));
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
                    "Invalid file name");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!allowedImageExtensions.contains(extension)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
                    "Unsupported image format. Allowed formats: " +
                    String.join(", ", allowedImageExtensions));
        }

        log.debug("Image file validation passed: {}", filename);
    }

    @Override
    public Map<String, Object> extractMusicMetadata(MultipartFile file) {
        Map<String, Object> metadata = new HashMap<>();
        File tempFile = null;

        try {
            // Create temporary file
            tempFile = File.createTempFile("music_", "_temp");
            file.transferTo(tempFile);

            // Read audio file
            AudioFile audioFile = AudioFileIO.read(tempFile);
            AudioHeader header = audioFile.getAudioHeader();
            Tag tag = audioFile.getTag();

            // Extract audio header information
            metadata.put("duration", header.getTrackLength()); // in seconds
            metadata.put("bitRate", header.getBitRate());
            metadata.put("sampleRate", header.getSampleRate());
            metadata.put("channels", header.getChannels());
            metadata.put("format", header.getFormat());

            // Extract tag information
            if (tag != null) {
                metadata.put("title", getTagValue(tag, FieldKey.TITLE));
                metadata.put("artist", getTagValue(tag, FieldKey.ARTIST));
                metadata.put("album", getTagValue(tag, FieldKey.ALBUM));
                metadata.put("year", getTagValue(tag, FieldKey.YEAR));
                metadata.put("genre", getTagValue(tag, FieldKey.GENRE));
                metadata.put("comment", getTagValue(tag, FieldKey.COMMENT));
            }

            log.info("Music metadata extracted successfully: {}", metadata);
            return metadata;

        } catch (Exception e) {
            log.warn("Failed to extract music metadata, using defaults: {}", e.getMessage());
            // Return default values if extraction fails
            metadata.put("duration", 0);
            metadata.put("title", "Unknown");
            metadata.put("artist", "Unknown");
            return metadata;

        } finally {
            // Clean up temporary file
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    log.warn("Failed to delete temporary file: {}", tempFile.getPath());
                }
            }
        }
    }

    @Override
    public String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        return uuid + "_" + timestamp + "." + extension;
    }

    @Override
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    @Override
    public boolean fileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        return Files.exists(path);
    }

    @Override
    public long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            return Files.size(path);
        } catch (IOException e) {
            log.error("Failed to get file size: {}", filePath, e);
            return 0;
        }
    }

    /**
     * Get tag value safely
     *
     * @param tag Tag object
     * @param key Field key
     * @return Tag value or empty string
     */
    private String getTagValue(Tag tag, FieldKey key) {
        try {
            String value = tag.getFirst(key);
            return (value != null && !value.trim().isEmpty()) ? value.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
