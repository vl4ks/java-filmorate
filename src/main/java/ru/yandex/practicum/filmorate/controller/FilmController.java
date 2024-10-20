package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest film) {
        log.debug("Создание нового фильма: {}", film);
        return filmService.createFilm(film);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable("id") long filmId) {
        return filmService.getFilmById(filmId);
    }


    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto newFilm) {
        return filmService.updateFilm(newFilm);
    }


    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.debug("Добавление лайка для фильма с id={} от пользователя с id={}", id, userId);
        return filmService.addLike(id, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId) {
        log.debug("Удаление лайка для фильма с id={} от пользователя с id={}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Получение самых {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}

