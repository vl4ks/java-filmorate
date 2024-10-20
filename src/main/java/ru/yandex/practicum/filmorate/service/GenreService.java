package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Collection<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(long genreId) {
        return genreRepository.findById(genreId);
    }

    public Genre createGenre(GenreDto newGenreDto) {
        return genreRepository.create(newGenreDto);
    }

    public void delete(Long id) {
        genreRepository.delete(id);
    }
}

