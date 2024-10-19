package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreStorage {
    Collection<Genre> findAll();

    Genre findById(Long id);

    Genre create(GenreDto newGenreDto);

    void delete(Long id);

    void createGenreFilmRelation(Long filmId, Set<Genre> genres);
}
