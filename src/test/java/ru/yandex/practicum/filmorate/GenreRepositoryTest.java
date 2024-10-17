package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.storage.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import(GenreRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("GenreDbStorage")
public class GenreRepositoryTest {
    private static final long TEST_GENRE_ID = 1L;
    private static final long TEST_FILM_ID = 1L;

    private final GenreRepository genreRepository;

    static Genre getTestGenre() {
        return Genre.builder()
                .id(TEST_GENRE_ID)
                .name("Комедия")
                .build();
    }

    @Test
    @DisplayName("должен находить жанр по id")
    public void should_return_genre_when_find_by_id() {
        Genre genre = genreRepository.findById(TEST_GENRE_ID);

        assertThat(genre)
                .usingRecursiveComparison()
                .isEqualTo(getTestGenre());
    }

    @Test
    @DisplayName("должен находить все жанры")
    public void should_return_all_genres() {
        Collection<Genre> genres = genreRepository.findAll();
        assertThat(genres).hasSize(2);
        assertThat(genres).extracting("name").containsExactlyInAnyOrder("Комедия", "Драма");
    }

    @Test
    @DisplayName("должен создавать новый жанр")
    public void should_create_new_genre() {
        GenreDto newGenre = new GenreDto();
        newGenre.setName("Триллер");

        Genre createdGenre = genreRepository.create(newGenre);
        assertThat(createdGenre.getId()).isNotNull();
        assertThat(createdGenre.getName()).isEqualTo("Триллер");
    }

    @Test
    @DisplayName("должен удалять жанр по id")
    public void should_delete_genre_by_id() {
        genreRepository.delete(TEST_GENRE_ID);
        assertThrows(NotFoundException.class, () -> genreRepository.findById(TEST_GENRE_ID));
    }

    @Test
    @DisplayName("должен находить жанры по id фильма")
    public void should_return_genres_by_film_id() {
        Set<Genre> genres = genreRepository.findGenresByFilmId(TEST_FILM_ID);
        assertThat(genres).hasSize(2);
        assertThat(genres).extracting("name").containsExactly("Комедия", "Драма");
    }

    @Test
    @DisplayName("должен создавать связь между фильмом и жанром")
    public void should_create_genre_and_film() {
        Long newFilmId = 2L;
        Set<Genre> newGenres = new HashSet<>();
        newGenres.add(new Genre(2L, "Драма"));

        genreRepository.createGenreFilmRelation(newFilmId, newGenres);

        Set<Genre> genres = genreRepository.findGenresByFilmId(newFilmId);
        assertThat(genres)
                .hasSize(1)
                .extracting("name")
                .containsExactlyInAnyOrder("Драма");
    }
}

