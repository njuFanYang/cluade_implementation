package com.musicshare.service.impl;

import com.musicshare.dto.request.CreatePlaylistRequest;
import com.musicshare.dto.request.UpdatePlaylistRequest;
import com.musicshare.dto.response.MusicResponse;
import com.musicshare.dto.response.PlaylistDetailResponse;
import com.musicshare.dto.response.PlaylistResponse;
import com.musicshare.entity.Music;
import com.musicshare.entity.Playlist;
import com.musicshare.entity.PlaylistMusic;
import com.musicshare.entity.User;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.MusicRepository;
import com.musicshare.repository.PlaylistMusicRepository;
import com.musicshare.repository.PlaylistRepository;
import com.musicshare.repository.UserRepository;
import com.musicshare.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Playlist Service Implementation
 *
 * <p>Implements business logic for playlist management operations.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicRepository playlistMusicRepository;
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request, Long creatorId) {
        log.info("Creating playlist: {} for user ID: {}", request.getName(), creatorId);

        // Validate creator exists
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Create playlist entity
        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setCoverImage(request.getCoverImage());
        playlist.setCreatorId(creatorId);
        playlist.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);

        playlist = playlistRepository.save(playlist);

        // Update user playlist count
        creator.setPlaylistCount(creator.getPlaylistCount() + 1);
        userRepository.save(creator);

        log.info("Playlist created successfully with ID: {}", playlist.getId());

        // Reload with relationships
        playlist = playlistRepository.findByIdWithCreator(playlist.getId()).orElse(playlist);

        return PlaylistResponse.fromEntity(playlist);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistDetailResponse getPlaylistDetail(Long id, Long userId) {
        log.debug("Getting playlist detail for ID: {} by user: {}", id, userId);

        Playlist playlist = playlistRepository.findByIdWithCreator(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        // Check access permission
        if (!playlist.isAccessibleBy(userId)) {
            log.warn("User {} attempted to access private playlist {}", userId, id);
            throw new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED);
        }

        // Get music list in order
        List<PlaylistMusic> playlistMusicList = playlistMusicRepository
                .findByPlaylistIdWithMusicAndGenre(id);

        List<MusicResponse> musicList = playlistMusicList.stream()
                .map(pm -> MusicResponse.fromEntity(pm.getMusic()))
                .collect(Collectors.toList());

        return PlaylistDetailResponse.fromEntity(playlist, musicList);
    }

    @Override
    @Transactional
    public PlaylistResponse updatePlaylist(Long id, UpdatePlaylistRequest request, Long userId) {
        log.info("Updating playlist ID: {} by user ID: {}", id, userId);

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        // Check permission: only owner can update
        if (!playlist.isOwnedBy(userId)) {
            log.warn("User {} attempted to update playlist {} without permission", userId, id);
            throw new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED);
        }

        // Update fields if provided
        if (StringUtils.hasText(request.getName())) {
            playlist.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            playlist.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getCoverImage())) {
            playlist.setCoverImage(request.getCoverImage());
        }
        if (request.getIsPublic() != null) {
            playlist.setIsPublic(request.getIsPublic());
        }

        playlist = playlistRepository.save(playlist);

        log.info("Playlist updated successfully: {}", id);

        // Reload with relationships
        playlist = playlistRepository.findByIdWithCreator(id).orElse(playlist);

        return PlaylistResponse.fromEntity(playlist);
    }

    @Override
    @Transactional
    public void deletePlaylist(Long id, Long userId) {
        log.info("Deleting playlist ID: {} by user ID: {}", id, userId);

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        // Check permission: only owner can delete
        if (!playlist.isOwnedBy(userId)) {
            log.warn("User {} attempted to delete playlist {} without permission", userId, id);
            throw new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED);
        }

        // Delete all playlist-music associations
        playlistMusicRepository.deleteByPlaylistId(id);

        // Update user playlist count
        User creator = userRepository.findById(playlist.getCreatorId()).orElse(null);
        if (creator != null && creator.getPlaylistCount() > 0) {
            creator.setPlaylistCount(creator.getPlaylistCount() - 1);
            userRepository.save(creator);
        }

        // Delete playlist
        playlistRepository.delete(playlist);

        log.info("Playlist deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public void addMusicToPlaylist(Long playlistId, Long musicId, Long userId) {
        log.info("Adding music {} to playlist {} by user {}", musicId, playlistId, userId);

        // Validate playlist exists and user has permission
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.isOwnedBy(userId)) {
            log.warn("User {} attempted to modify playlist {} without permission", userId, playlistId);
            throw new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED);
        }

        // Validate music exists
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));

        // Check if music already in playlist
        if (playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId)) {
            throw new BusinessException(ErrorCode.MUSIC_ALREADY_IN_PLAYLIST);
        }

        // Get next position
        Integer maxPosition = playlistMusicRepository.getMaxPosition(playlistId);
        int nextPosition = (maxPosition == null) ? 0 : maxPosition + 1;

        // Create playlist-music association
        PlaylistMusic playlistMusic = new PlaylistMusic();
        playlistMusic.setPlaylistId(playlistId);
        playlistMusic.setMusicId(musicId);
        playlistMusic.setPosition(nextPosition);

        playlistMusicRepository.save(playlistMusic);

        // Update playlist music count
        playlist.incrementMusicCount();
        playlistRepository.save(playlist);

        log.info("Music {} added to playlist {} at position {}", musicId, playlistId, nextPosition);
    }

    @Override
    @Transactional
    public void removeMusicFromPlaylist(Long playlistId, Long musicId, Long userId) {
        log.info("Removing music {} from playlist {} by user {}", musicId, playlistId, userId);

        // Validate playlist exists and user has permission
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.isOwnedBy(userId)) {
            log.warn("User {} attempted to modify playlist {} without permission", userId, playlistId);
            throw new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED);
        }

        // Check if music in playlist
        if (!playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId)) {
            throw new BusinessException(ErrorCode.MUSIC_NOT_IN_PLAYLIST);
        }

        // Delete playlist-music association
        playlistMusicRepository.deleteByPlaylistIdAndMusicId(playlistId, musicId);

        // Update playlist music count
        playlist.decrementMusicCount();
        playlistRepository.save(playlist);

        // Reorder remaining music
        List<PlaylistMusic> remainingMusic = playlistMusicRepository
                .findByPlaylistIdOrderByPositionAsc(playlistId);

        for (int i = 0; i < remainingMusic.size(); i++) {
            PlaylistMusic pm = remainingMusic.get(i);
            pm.setPosition(i);
            playlistMusicRepository.save(pm);
        }

        log.info("Music {} removed from playlist {}", musicId, playlistId);
    }

    @Override
    @Transactional
    public void reorderPlaylistMusic(Long playlistId, List<Long> musicIds, Long userId) {
        log.info("Reordering playlist {} music by user {}", playlistId, userId);

        // Validate playlist exists and user has permission
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.isOwnedBy(userId)) {
            log.warn("User {} attempted to modify playlist {} without permission", userId, playlistId);
            throw new BusinessException(ErrorCode.PLAYLIST_PERMISSION_DENIED);
        }

        // Get current playlist music
        List<PlaylistMusic> currentMusic = playlistMusicRepository
                .findByPlaylistIdOrderByPositionAsc(playlistId);

        // Validate that all music IDs are present
        if (currentMusic.size() != musicIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER,
                    "Music IDs count doesn't match playlist");
        }

        // Update positions
        for (int i = 0; i < musicIds.size(); i++) {
            Long musicId = musicIds.get(i);

            PlaylistMusic pm = currentMusic.stream()
                    .filter(m -> m.getMusicId().equals(musicId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_IN_PLAYLIST));

            pm.setPosition(i);
            playlistMusicRepository.save(pm);
        }

        log.info("Playlist {} reordered successfully", playlistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistResponse> getPlaylistsByCreator(Long creatorId, Pageable pageable) {
        log.debug("Getting playlists by creator ID: {}", creatorId);

        // Validate creator exists
        userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return playlistRepository.findByCreatorId(creatorId, pageable)
                .map(PlaylistResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistResponse> getPublicPlaylists(Pageable pageable) {
        log.debug("Getting public playlists");

        return playlistRepository.findByIsPublicTrue(pageable)
                .map(PlaylistResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistResponse> searchPlaylists(String keyword, Pageable pageable) {
        log.debug("Searching playlists with keyword: {}", keyword);

        return playlistRepository.searchPublicPlaylists(keyword, pageable)
                .map(PlaylistResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistResponse> getPopularPlaylists(Pageable pageable) {
        log.debug("Getting popular playlists");

        return playlistRepository.findPopularPlaylists(pageable)
                .map(PlaylistResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessPlaylist(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        return playlist.isAccessibleBy(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOwner(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        return playlist.isOwnedBy(userId);
    }
}
