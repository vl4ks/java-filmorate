package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.LinkedHashSet;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {
    private static final String GET_ALL_GENRES = "SELECT * FROM GENRES;";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM GENRES WHERE genre_id = ?";
    private static final String CREATE_GENRE = "INSERT INTO GENRES (name) VALUES (?)";
    private static final String DELETE_GENRE = "DELETE FROM GENRES WHERE genre_id = ?";
    private static String GET_FILM_GENRES = """
            SELECT g.genre_id, g.name
            FROM FILM_GENRES fg
            JOIN GENRES g ON fg.genre_id = g.genre_id
            WHERE fg.film_id = ?
            """;
    private static final String CREATE_GENRE_FILM = "INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?)";

    public GenreRepository(JdbcTemplate jdbc) {
        super(jdbc, new GenreRowMapper());
    }

    public Collection<Genre> findAll() {
        log.debug("Получение всех жанров");
        return findMany(GET_ALL_GENRES);
    }

    public Genre findById(Long id) {
        log.debug("Поиск жанра с id={}", id);
        return findOne(GET_GENRE_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Жанр фильма с id " + id + " не найден."));

    }


    public Genre create(GenreDto newGenreDto) {
        log.debug("Создание жанра: {}", newGenreDto);
        long id = insert(CREATE_GENRE, newGenreDto.getName());
        return findById(id);
    }

    public void delete(Long id) {
        log.debug("Удаление жанра: {}", id);
        jdbc.update(DELETE_GENRE, id);
    }

    public Set<Genre> findGenresByFilmId(Long filmId) {
        log.debug("Получаем список всех жанров определенного фильма.");
        return new LinkedHashSet<>(findMany(GET_FILM_GENRES, filmId));
    }

    public void createGenreFilmRelation(Long filmId, Set<Genre> genres) {
        log.debug("Объединяем фильм и его жанры по их id.");
        for (Genre genre : genres) {
            isGenreExists(genre.getId());
            try {
                insert(CREATE_GENRE_FILM, filmId, genre.getId());
            } catch (InvalidDataAccessApiUsageException ignored) {
            }
        }
    }

    private void isGenreExists(Long genreId) {
        log.debug("Проверяем жанр на наличие в БД.");
        if (!findOne("SELECT * FROM GENRES WHERE genre_id = ?", genreId).isPresent()) {
            throw new ParameterNotValidException("В БД нет жанра фильма с id ");
        }
    }
}

