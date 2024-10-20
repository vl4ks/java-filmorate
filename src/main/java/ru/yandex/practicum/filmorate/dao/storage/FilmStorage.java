package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAllWithRatings();

    Film create(NewFilmRequest film);

    Film update(FilmDto newFilm);

    Film findById(Long id);

    Collection<Film> getPopularFilms(int count);
}
