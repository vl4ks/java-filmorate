package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(NewFilmRequest film);

    Film update(FilmDto newFilm);

    Film findById(Long id);

    Film addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(int count);

    Set<Long> getLikes(Long filmId);
}
