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
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILMS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILMS WHERE id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO LIKES (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
    private static final String CREATE_FILM_QUERY = "INSERT INTO FILMS (name, description, release_date, duration) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE FILMS SET name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE id = ?";

    private static final String GET_POPULAR_FILMS_QUERY = """
           SELECT f.*, COUNT(fl.user_id) AS like_count
           FROM FILMS AS f
           LEFT JOIN LIKES AS fl ON f.id = fl.film_id
           GROUP BY f.id
           ORDER BY like_count DESC, f.id ASC
           LIMIT ?;
           """;
    private static final String GET_LIKES_QUERY = "SELECT user_id FROM LIKES WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
    }

    public Collection<Film> findAll() {
        log.debug("Получение всех фильмов");
        return findMany(FIND_ALL_QUERY);
    }

    public Film findById(Long id) {
        log.debug("Поиск фильма с id={}", id);
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден."));
    }

    public Film addLike(Long filmId, Long userId) {
        log.debug("Добавление лайка: filmId={}, userId={}", filmId, userId);
        update(ADD_LIKE_QUERY, filmId, userId);
        return findById(filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаление лайка: filmId={}, userId={}", filmId, userId);
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public Film create(NewFilmRequest film) {
        log.debug("Создание фильма: {}", film);
        long id = insert(CREATE_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration());
        return findById(id);
    }

    public Film update(FilmDto newFilm) {
        log.debug("Обновление фильма: {}", newFilm);
        update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                newFilm.getDuration(), newFilm.getId());
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

    public Set<Long> getLikes(Long filmId) {
        log.debug("Получение лайков для фильма={}", filmId);
        return new HashSet<>(jdbc.queryForList(GET_LIKES_QUERY, Long.class, filmId));
    }
}

