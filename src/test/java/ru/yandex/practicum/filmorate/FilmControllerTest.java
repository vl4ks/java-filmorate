package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilmControllerTest extends FilmorateApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
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
        Film film = new Film(null, "Film Name", "Description",
                LocalDate.of(1967, 3, 25), 100);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testCreateFilmValidationFailure() throws Exception {
        Film film = new Film(null, "", "Description",
                LocalDate.of(2010, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        Film film = new Film(1L, "Film Name", "Description",
                LocalDate.of(1967, 3, 25), 100);
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        Film updatedFilm = new Film(1L, "Updated Film Name", "Updated Description",
                LocalDate.of(2010, 1, 1), 120);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film Name"));
    }

    @Test
    public void testUpdateFilmNotFound() throws Exception {
        Film updatedFilm = new Film(999L, "Updated Film Name", "Updated Description",
                LocalDate.of(2010, 1, 1), 120);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmController.update(updatedFilm);
        });

        assertEquals("Фильм с id = 999 не найден", exception.getMessage());
    }
}

