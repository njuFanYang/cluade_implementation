package com.musicshare.service.impl;

import com.musicshare.dto.response.GenreResponse;
import com.musicshare.entity.Genre;
import com.musicshare.exception.BusinessException;
import com.musicshare.exception.ErrorCode;
import com.musicshare.repository.GenreRepository;
import com.musicshare.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Genre Service Implementation
 *
 * <p>Implements business logic for genre management operations.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        log.debug("Getting all genres");

        return genreRepository.findAllByOrderByNameAsc().stream()
                .map(GenreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponse> getPopularGenres() {
        log.debug("Getting popular genres");

        return genreRepository.findAllByOrderByMusicCountDesc().stream()
                .map(GenreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Long id) {
        log.debug("Getting genre by ID: {}", id);

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.GENRE_NOT_FOUND));

        return GenreResponse.fromEntity(genre);
    }
}
