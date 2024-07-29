package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilmControllerTest extends FilmorateApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    private FilmService filmService;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
    }

    @Test
    public void testFindAllFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testCreateFilm() throws Exception {
        Film film = Film.builder()
                .id(null)
                .name("Film Name")
                .description("Description")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testCreateFilmValidationFailure() throws Exception {
        Film film = Film.builder()
                .id(null)
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(120)
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации: Название не может быть пустым"));
    }

    @Test
    public void testCreateFilmWithEmptyDescription() throws Exception {
        Film film = Film.builder()
                .id(null)
                .name("Film Name")
                .description(null)
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(120)
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации: Описание не может быть пустым и длиннее 200 символов"));
    }

    @Test
    public void testCreateFilmWithLongDescription() throws Exception {
        String longDescription = "a".repeat(201);
        Film film = Film.builder()
                .id(null)
                .name("Film Name")
                .description(longDescription)
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(120)
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации: Описание не может быть пустым и длиннее 200 символов"));
    }

    @Test
    public void testCreateFilmWithEarlyReleaseDate() throws Exception {
        Film film = Film.builder()
                .id(null)
                .name("Film Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800, 1, 1))
                .duration(100)
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации: Дата релиза - не раньше  28 декабря 1895 года"));
    }

    @Test
    public void testCreateFilmWithNegativeDuration() throws Exception {
        Film film = Film.builder()
                .id(null)
                .name("Film Name")
                .description("Description")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(-100)
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации: Продолжительность фильма должна быть положительным числом"));
    }

    @Test
    public void testUpdateFilm() throws Exception {
        Film film = Film.builder()
                .id(1L)
                .name("Film Name")
                .description("Description")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Updated Film Name")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(120)
                .build();
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film Name"));
    }

    @Test
    public void testUpdateFilmNotFound() throws Exception {
        Film updatedFilm = Film.builder()
                .id(999L)
                .name("Updated Film Name")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(120)
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmController.update(updatedFilm);
        });

        assertEquals("Фильм с id = 999 не найден", exception.getMessage());
    }
}

