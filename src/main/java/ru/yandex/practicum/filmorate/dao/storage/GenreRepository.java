package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {
    private static final String GET_ALL_GENRES = "SELECT * FROM GENRES;";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM GENRES WHERE genre_id = ?";
    private static final String CREATE_GENRE = "INSERT INTO GENRES (name) VALUES (?)";
    private static final String DELETE_GENRE = "DELETE FROM GENRES WHERE genre_id = ?";
    private static final String CREATE_GENRE_FILM = "INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?)";

    private static final String FIND_GENRES_BY_FILM_IDS_QUERY = """
        SELECT FILM_GENRES.film_id, GENRES.genre_id, GENRES.name
        FROM FILM_GENRES
        JOIN GENRES ON FILM_GENRES.genre_id = GENRES.genre_id
        WHERE FILM_GENRES.film_id IN (:filmIds)
        """;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public GenreRepository(JdbcTemplate jdbc) {
        super(jdbc, new GenreRowMapper());
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbc);
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
        Map<Long, Set<Genre>> genresByFilmId = findGenresByFilmIds(Set.of(filmId));

        return genresByFilmId.getOrDefault(filmId, Collections.emptySet())
                .stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<Long, Set<Genre>> findGenresByFilmIds(Set<Long> filmIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("filmIds", filmIds);
        List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(FIND_GENRES_BY_FILM_IDS_QUERY, parameters);

        Map<Long, Set<Genre>> genresByFilmId = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long filmId = ((Number) row.get("film_id")).longValue();
            Long genreId = ((Number) row.get("genre_id")).longValue();
            String genreName = (String) row.get("name");
            genresByFilmId.computeIfAbsent(filmId, id -> new HashSet<>()).add(new Genre(genreId, genreName));
        }

        return genresByFilmId;
    }

    public void createGenreFilmRelation(Long filmId, Set<Genre> genres) {
        log.debug("Объединяем фильм и его жанры по их id.");
        jdbc.update("DELETE FROM FILM_GENRES WHERE FILM_ID = ?", filmId);
        for (Genre genre : genres) {
            isGenreExists(genre.getId());
        }

        List<Object[]> batchArgs = genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .collect(Collectors.toList());

        try {
            jdbc.batchUpdate(CREATE_GENRE_FILM, batchArgs);
        } catch (InvalidDataAccessApiUsageException ex) {
            log.error("Ошибка при создании связи фильма с жанрами", ex);
        }
    }


    private void isGenreExists(Long genreId) {
        log.debug("Проверяем жанр на наличие в БД.");
        if (findOne("SELECT * FROM GENRES WHERE genre_id = ?", genreId).isEmpty()) {
            throw new ParameterNotValidException("В БД нет жанра фильма с id ");
        }
    }
}

