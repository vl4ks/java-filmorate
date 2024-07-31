package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;
    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);

        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .likes(new HashSet<>(Set.of(1L, 2L)))
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Film2")
                .likes(new HashSet<>(Set.of(1L)))
                .description("Description2")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        Film film3 = Film.builder()
                .id(3L)
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(2002, 1, 1))
                .duration(90)
                .build();

        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.create(film3);

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1985, 1, 1))
                .build();
        User user3 = User.builder()
                .id(3L)
                .email("user3@example.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
    }

    @Test
    public void testAddLike() {
        filmService.addLike(1L, 1L);

        Film film = filmStorage.findById(1L).orElseThrow();
        assertTrue(film.getLikes().contains(1L));
    }

    @Test
    public void testRemoveLike() {
        filmService.addLike(1L, 1L);
        filmService.removeLike(1L, 1L);

        Film film = filmStorage.findById(1L).orElseThrow();
        assertFalse(film.getLikes().contains(1L));
    }

    @Test
    public void testGetTopFilms() {
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(2L, 1L);
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmService.addLike(3L, 3L);

        List<Film> topFilms = filmService.getPopularFilms(2);

        assertEquals(2, topFilms.size());
        assertEquals("Film3", topFilms.get(0).getName());
        assertEquals("Film1", topFilms.get(1).getName());
    }
}
