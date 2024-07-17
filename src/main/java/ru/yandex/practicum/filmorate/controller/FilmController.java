package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Проверка условий на добавление фильма");
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка при заполнении name: пустое");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Ошибка при заполнении description: пустое");
            throw new ValidationException("Описание не может быть пустым и длиннее 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при заполнении releaseDate: недопустимое или пустое значение");
            throw new ValidationException("Дата релиза - не раньше  28 декабря 1895 года");

        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка при заполнении duration: недопустимое значение");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");

        }
        log.info("Добавляем новый фильм");
        film.setId(idCounter++);
        film.setReleaseDate(film.getReleaseDate());
        film.setDuration(film.getDuration());
        films.put(film.getId(), film);
        log.info("Фильм добавлен!");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Проверка условий на обновление фильма");
        if (newFilm.getId() == null) {
            log.error("Пустой id");
            throw new ValidationException("Id должен быть указан");
        }

        log.info("Проверка наличия фильма в коллекции");
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            log.info("Проверка и обновление name");
            if (newFilm.getName() != null) {
                if (newFilm.getName().isBlank()) {
                    log.error("Ошибка при заполнении name: пустое");
                    throw new ValidationException("Название не может быть пустым");
                }
                oldFilm.setName(newFilm.getName());
            }

            log.info("Проверка и обновление description");
            if (newFilm.getDescription() != null) {
                if (newFilm.getDescription().length() > 200) {
                    log.error("Ошибка при заполнении description: слишком длинное");
                    throw new ValidationException("Описание не может быть длиннее 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }

            log.info("Проверка и обновление releaseDate");
            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                    log.error("Ошибка при заполнении releaseDate: слишком ранняя дата");
                    throw new ValidationException("Дата релиза - не раньше  28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }

            log.info("Проверка и обновление duration");
            if (newFilm.getDuration() != -1) {
                if (newFilm.getDuration() <= 0) {
                    log.error("Ошибка при заполнении duration: не положительное число");
                    throw new ValidationException("Продолжительность фильма должна быть положительным числом");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм обновлен!");
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
