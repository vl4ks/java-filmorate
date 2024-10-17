package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.storage.FilmRepository;
import ru.yandex.practicum.filmorate.dao.storage.GenreRepository;
import ru.yandex.practicum.filmorate.dao.storage.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@Import({FilmRepository.class, GenreRepository.class, MpaRatingRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("FilmRepository")
public class FilmRepositoryTest {
    public static final long TEST_FILM_ID = 1L;
    private final FilmRepository filmRepository;

    static Film getTestFilm() {
        Film film = new Film();
        film.setId(TEST_FILM_ID);
        film.setName("Film One");
        film.setDescription("Description for Film One");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120L);

        return film;
    }

    @Test
    @DisplayName("должен находить фильм по id")
    public void should_return_film_when_find_by_id() {
        Film film = filmRepository.findById(TEST_FILM_ID);
        assertThat(film)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("должен создавать новый фильм")
    public void should_create_new_film() {
        NewFilmRequest newFilm = new NewFilmRequest();
        newFilm.setName("New Film");
        newFilm.setDescription("Description for New Film");
        newFilm.setReleaseDate(LocalDate.of(2021, 10, 10));
        newFilm.setDuration(150L);

        Film createdFilm = filmRepository.create(newFilm);
        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newFilm);
    }

    @Test
    @DisplayName("должен находить все фильмы")
    public void should_return_all_films() {
        Collection<Film> films = filmRepository.findAll();
        assertThat(films).hasSize(2);
        assertThat(films.iterator().next())
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }


    @Test
    @DisplayName("должен обновлять существующий фильм")
    public void should_update_film() {
        Film filmToUpdate = getTestFilm();
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(2L);
        mpaRating.setName("PG");

        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1L, "Комедия"));

        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        filmToUpdate.setName("Updated Film");
        filmToUpdate.setDescription("Updated Description");
        filmToUpdate.setReleaseDate(LocalDate.of(2001, 1, 1));
        filmToUpdate.setDuration(130L);

        FilmDto filmDtoToUpdate = FilmMapper.mapToFilmDto(filmToUpdate, mpaRating, genres, likes);

        Film updatedFilm = filmRepository.update(filmDtoToUpdate);

        assertThat(updatedFilm)
                .usingRecursiveComparison()
                .isEqualTo(filmToUpdate);
    }


    @Test
    @DisplayName("должен удалять фильм по id")
    public void should_delete_film_by_id() {
        filmRepository.delete(getTestFilm().getId());
        Collection<Film> films = filmRepository.findAll();
        assertThat(films).extracting(Film::getId).doesNotContain(1L);
    }

    @Test
    @DisplayName("должен добавлять лайк фильму")
    public void should_add_like_to_film() {
        Film film = getTestFilm();

        filmRepository.addLike(film.getId(), 1L);

        Set<Long> likes = filmRepository.getLikes(film.getId());

        assertThat(likes.size() == 1);
    }

    @Test
    @DisplayName("должен удалять лайк у фильма")
    public void should_remove_like_from_film() {
        Film film = getTestFilm();

        filmRepository.removeLike(film.getId(), 1L);

        Set<Long> likes = filmRepository.getLikes(film.getId());

        assertThat(likes.isEmpty());
    }


    @Test
    @DisplayName("должен находить 10 популярных фильмов")
    public void should_return_top_10_popular_films() {
        Set<Genre> genres1 = new HashSet<>(Set.of(new Genre(1L, "Комедия")));
        NewFilmRequest newFilm1 = new NewFilmRequest("Film 1", "Description for Film 1",
                LocalDate.of(2001, 1, 1), 120L, new MpaRating(1L, "G"),
                genres1, new HashSet<>());
        Film film1 = filmRepository.create(newFilm1);

        Set<Genre> genres2 = new HashSet<>(Set.of(new Genre(2L, "Драма")));
        NewFilmRequest newFilm2 = new NewFilmRequest("Film 2", "Description for Film 2",
                LocalDate.of(2002, 2, 2), 150L, new MpaRating(2L, "PG"),
                genres2, new HashSet<>());
        Film film2 = filmRepository.create(newFilm2);

        Set<Genre> genres3 = new HashSet<>(Set.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));
        NewFilmRequest newFilm3 = new NewFilmRequest("Film 3", "Description for Film 3",
                LocalDate.of(2003, 3, 3), 180L, new MpaRating(3L, "PG-13"),
                genres3, new HashSet<>());
        Film film3 = filmRepository.create(newFilm3);

        filmRepository.addLike(film1.getId(), 1L);
        filmRepository.addLike(film1.getId(), 2L);
        filmRepository.addLike(film2.getId(), 1L);
        filmRepository.addLike(film3.getId(), 1L);
        filmRepository.addLike(film3.getId(), 2L);
        filmRepository.addLike(film3.getId(), 3L);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(10);
        System.out.println(popularFilms);
        assertThat(popularFilms).hasSizeLessThanOrEqualTo(10);

        List<Long> expectedFilmIds = List.of(
                film3.getId(),
                film1.getId(),
                film2.getId(),
                1L,
                2L
        );

        assertThat(popularFilms).extracting(Film::getId).containsExactlyInAnyOrderElementsOf(expectedFilmIds);

    }
}

