package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.storage.FilmRepository;
import ru.yandex.practicum.filmorate.dao.storage.GenreRepository;
import ru.yandex.practicum.filmorate.dao.storage.LikeRepository;
import ru.yandex.practicum.filmorate.dao.storage.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@Import({FilmRepository.class, GenreRepository.class, MpaRatingRepository.class, LikeRepository.class, FilmService.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("FilmRepository")
public class FilmRepositoryTest {
    public static final long TEST_FILM_ID = 1L;
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;
    private final FilmService filmService;

    static Film getTestFilm() {
        Film film = new Film();
        film.setId(TEST_FILM_ID);
        film.setName("Film One");
        film.setDescription("Description for Film One");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120L);

        MpaRating mpaRating = new MpaRating(1L, "G");
        film.setMpa(mpaRating);
        return film;
    }

    @Test
    @DisplayName("должен находить фильм по id")
    public void should_return_film_when_find_by_id() {
        Film film = filmRepository.findById(TEST_FILM_ID);
        assertThat(film)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("mpa.name")
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("должен создавать новый фильм и связывать с жанрами")
    public void should_create_new_film_with_genres() {
        NewFilmRequest newFilm = new NewFilmRequest();
        newFilm.setName("New Film");
        newFilm.setDescription("Description for New Film");
        newFilm.setReleaseDate(LocalDate.of(2021, 10, 10));
        newFilm.setDuration(150L);

        MpaRating mpaRating = new MpaRating(1L, "G");
        newFilm.setMpa(mpaRating);

        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1L, "Комедия"));
        newFilm.setGenres(genres);

        FilmDto createdFilmDto = filmService.createFilm(newFilm);

        assertThat(createdFilmDto).isNotNull();

        Set<Genre> filmGenres = genreRepository.findGenresByFilmId(createdFilmDto.getId());
        assertThat(filmGenres).containsExactlyInAnyOrderElementsOf(genres);
    }


    @Test
    @DisplayName("должен находить все фильмы")
    public void should_return_all_films() {
        Collection<Film> films = filmRepository.findAllWithRatingsAndLikes();
        assertThat(films).hasSize(2);
        assertThat(films.iterator().next())
                .usingRecursiveComparison()
                .ignoringFields("mpa.name")
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("должен возвращать фильмы с количеством лайков")
    public void should_return_film_with_like_count() {
        Film film = filmRepository.findById(TEST_FILM_ID);
        assertThat(film.getLikesCount()).isEqualTo(likeRepository.getLikeCount(TEST_FILM_ID));
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

        filmToUpdate.setName("Updated Film");
        filmToUpdate.setDescription("Updated Description");
        filmToUpdate.setReleaseDate(LocalDate.of(2001, 1, 1));
        filmToUpdate.setDuration(130L);
        filmToUpdate.setMpa(mpaRating);

        FilmDto filmDtoToUpdate = FilmMapper.mapToFilmDto(filmToUpdate, mpaRating, genres,
                likeRepository.getLikeCount(filmToUpdate.getId()));

        Film updatedFilm = filmRepository.update(filmDtoToUpdate);

        assertThat(updatedFilm)
                .usingRecursiveComparison()
                .ignoringFields("mpa.name")
                .isEqualTo(filmToUpdate);
    }


    @Test
    @DisplayName("должен удалять фильм по id")
    public void should_delete_film_by_id() {
        filmRepository.delete(getTestFilm().getId());
        Collection<Film> films = filmRepository.findAllWithRatingsAndLikes();
        assertThat(films).extracting(Film::getId).doesNotContain(1L);
    }

    @Test
    @DisplayName("должен добавлять лайк фильму")
    public void should_add_like_to_film() {
        likeRepository.addLike(TEST_FILM_ID, 1L);
        int likeCount = likeRepository.getLikeCount(TEST_FILM_ID);
        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("должен удалять лайк у фильма")
    public void should_remove_like_from_film() {
        Film film = getTestFilm();

        likeRepository.removeLike(film.getId(), 1L);

        Integer likes = likeRepository.getLikeCount(film.getId());

        assertThat(likes).isZero();
    }


    @Test
    @DisplayName("должен находить 10 популярных фильмов")
    public void should_return_top_10_popular_films() {
        Set<Genre> genres1 = new HashSet<>(Set.of(new Genre(1L, "Комедия")));
        NewFilmRequest newFilm1 = new NewFilmRequest("Film 1", "Description for Film 1",
                LocalDate.of(2001, 1, 1), 120L, new MpaRating(1L, "G"), genres1);
        Film film1 = filmRepository.create(newFilm1);

        Set<Genre> genres2 = new HashSet<>(Set.of(new Genre(2L, "Драма")));
        NewFilmRequest newFilm2 = new NewFilmRequest("Film 2", "Description for Film 2",
                LocalDate.of(2002, 2, 2), 150L, new MpaRating(2L, "PG"), genres2);
        Film film2 = filmRepository.create(newFilm2);

        Set<Genre> genres3 = new HashSet<>(Set.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));
        NewFilmRequest newFilm3 = new NewFilmRequest("Film 3", "Description for Film 3",
                LocalDate.of(2003, 3, 3), 180L, new MpaRating(3L, "PG-13"), genres3);
        Film film3 = filmRepository.create(newFilm3);

        likeRepository.addLike(film1.getId(), 1L);
        likeRepository.addLike(film1.getId(), 2L);
        likeRepository.addLike(film2.getId(), 1L);
        likeRepository.addLike(film3.getId(), 1L);
        likeRepository.addLike(film3.getId(), 2L);
        likeRepository.addLike(film3.getId(), 3L);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(10);

        assertThat(popularFilms).hasSizeLessThanOrEqualTo(10);

        List<Long> expectedFilmIds = List.of(
                film3.getId(),
                film1.getId(),
                film2.getId()
        );

        assertThat(popularFilms.stream().limit(3).map(Film::getId))
                .containsExactlyElementsOf(expectedFilmIds);

        if (popularFilms.size() > 3) {
            List<Long> additionalFilmIds = popularFilms.stream()
                    .skip(3)
                    .map(Film::getId)
                    .toList();

            assertThat(additionalFilmIds).containsExactlyInAnyOrder(1L, 2L);
        }
    }
}

