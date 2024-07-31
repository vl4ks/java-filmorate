package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Getting all films");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Creating film: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.debug("Updating film: {}", newFilm);
        return filmService.update(newFilm);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.debug("Deleting film with id={}", id);
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable Long id, @PathVariable Long userId) {
            log.debug("Adding like to film with id: {} by user with id: {}", id, userId);
            filmService.addLike(id, userId);
            return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> removeLike(@PathVariable Long id, @PathVariable Long userId) {
            log.debug("Removing like from film with id: {} by user with id: {}", id, userId);
            filmService.removeLike(id, userId);
            return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Getting popular films, count: {}", count);
        return filmService.getPopularFilms(count);
    }
}
