package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_WITH_RATINGS_LIKES_QUERY = """
        SELECT f.*, mpa.id as mpa_id, mpa.name as mpa_name, COUNT(l.user_id) as likes_count
        FROM FILMS f
        JOIN MPA_RATINGS mpa ON f.mpa_id = mpa.id
        LEFT JOIN LIKES l ON f.id = l.film_id
        GROUP BY f.id, mpa.id
        """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILMS WHERE id = ?";
    private static final String CREATE_FILM_QUERY = """
        INSERT INTO FILMS (name, description, release_date, duration, mpa_id)
        VALUES (?, ?, ?, ?, ?)
        """;
    private static final String UPDATE_FILM_QUERY = """
        UPDATE FILMS SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
        WHERE id = ?
        """;
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE id = ?";
    private static final String GET_POPULAR_FILMS_QUERY = """
           SELECT f.*, COUNT(fl.user_id) AS like_count
           FROM FILMS AS f
           LEFT JOIN LIKES AS fl ON f.id = fl.film_id
           GROUP BY f.id
           ORDER BY like_count DESC, f.id ASC
           LIMIT ?;
           """;

    public FilmRepository(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
    }


    public Film findById(Long id) {
        log.debug("Поиск фильма с id={}", id);
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден."));
    }

    public Collection<Film> findAllWithRatingsAndLikes() {
        log.debug("Получение всех фильмов с рейтингами и количеством лайков");
        return findMany(FIND_ALL_WITH_RATINGS_LIKES_QUERY);
    }

    public Film create(NewFilmRequest film) {
        log.debug("Создание фильма: {}", film);
        long id = insert(CREATE_FILM_QUERY, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        return findById(id);
    }

    public Film update(FilmDto newFilm) {
        log.debug("Обновление фильма: {}", newFilm);
        update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(),
                newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(),
                newFilm.getId());
        return findById(newFilm.getId());
    }

    public void delete(Long id) {
        log.debug("Удаление фильма с id={}", id);
        jdbc.update(DELETE_FILM_QUERY, id);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Получение топ фильмов, количество={}", count);
        return findMany(GET_POPULAR_FILMS_QUERY, count);
    }
}

