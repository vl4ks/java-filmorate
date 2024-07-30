package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        log.debug("Getting all films");
        return filmStorage.findAll();
    }

    public Film create(@Valid @RequestBody Film film) {
        log.debug("Creating film: {}", film);
        return filmStorage.create(film);
    }

    public Film update(@Valid @RequestBody Film newFilm) {
        log.debug("Updating film: {}", newFilm);
        return filmStorage.update(newFilm);
    }

    public void delete(@PathVariable Long id) {
        log.debug("Deleting film with id={}", id);
        filmStorage.delete(id);
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Adding like: filmId={}, userId={}", filmId, userId);
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        film.getLikes().add(userId);
        filmStorage.update(film);
        log.debug("Like added: filmId={}, userId={}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Removing like: filmId={}, userId={}", filmId, userId);
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.debug("Like removed: filmId={}, userId={}", filmId, userId);
    }


    public List<Film> getPopularFilms(int count) {
        log.debug("Getting top films, count={}", count);
        List<Film> topFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Found top films: count={}, topFilms={}", count, topFilms);
        return topFilms;
    }
}
