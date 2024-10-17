package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaRatingRepository extends BaseRepository<MpaRating> implements MpaRatingStorage {
    private static final String GET_ALL_RATINGS = "SELECT * FROM MPA_RATINGS ORDER BY id";
    private static final String GET_RATING_BY_ID = "SELECT * FROM MPA_RATINGS WHERE id = ?";
    private static final String CREATE_RATING = "INSERT INTO MPA_RATINGS (name) VALUES (?)";
    private static final String DELETE_RATING = "DELETE FROM MPA_RATINGS WHERE id = ?";
    private static final String CREATE_MPA_FILM = "INSERT INTO FILM_RATINGS (film_id, mpa_rating_id) VALUES (?, ?)";
    static String GET_FILM_MPA = """
            SELECT r.* FROM MPA_RATINGS r
            JOIN FILM_RATINGS fr ON r.id = fr.mpa_rating_id
            WHERE fr.film_id = ?
            ORDER BY r.id ASC
            """;
    private static final String MPA_AND_FILM_EXISTS_QUERY = """
            SELECT * FROM mpa_ratings AS mr
            JOIN FILM_RATINGS AS fr ON mr.id = fr.mpa_rating_id
            WHERE fr.film_id = ?
            """;

    public MpaRatingRepository(JdbcTemplate jdbc) {
        super(jdbc, new MpaRatingRowMapper());
    }

    public Collection<MpaRating> findAll() {
        log.debug("Получение списка рейтингов");
        return findMany(GET_ALL_RATINGS);
    }

    public MpaRating findById(Long id) {
        log.debug("Поиск рейтинга по id={}", id);
        return findOne(GET_RATING_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден."));
    }

    public MpaRating findRatingByFilmId(Long filmId) {
        log.debug("Поиск рейтинга определенного фильма по его id.");
        return findOne(GET_FILM_MPA, filmId)
                .orElse(new MpaRating());
    }

    public MpaRating create(MpaRatingDto newMpaRatingDto) {
        log.debug("Создание рейтинга: {}", newMpaRatingDto);
        long id = insert(CREATE_RATING, newMpaRatingDto.getName());

        return findById(id);
    }

    public void delete(Long id) {
        log.debug("Удаление рейтинга с id={}", id);
        update(DELETE_RATING, id);
    }

    public void createMpaFilmRelation(Long filmId, Long ratingId) {
        log.info("Создание связи между фильмом {} и рейтингом: {}", filmId, ratingId);
        if (ratingFilmRelationExists(filmId).isPresent()) {
            update("DELETE FROM FILM_RATINGS WHERE film_id = ?", filmId);
        }
        try {
            insert(CREATE_MPA_FILM, filmId, ratingId);
            log.info("Связь между фильмом и рейтингом создана");
        } catch (InvalidDataAccessApiUsageException ignored) {
        }
    }

    private Optional<MpaRating> ratingFilmRelationExists(Long filmId) {
        log.debug("Проверяем фильм и рейтинг на наличие в БД");
        return findOne(MPA_AND_FILM_EXISTS_QUERY, filmId);
    }
}

