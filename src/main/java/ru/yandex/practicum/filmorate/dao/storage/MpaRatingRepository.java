package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaRatingRepository extends BaseRepository<MpaRating> implements MpaRatingStorage {
    private static final String GET_ALL_RATINGS = "SELECT * FROM MPA_RATINGS ORDER BY id";
    private static final String GET_RATING_BY_ID = "SELECT * FROM MPA_RATINGS WHERE id = ?";
    private static final String CREATE_RATING = "INSERT INTO MPA_RATINGS (name) VALUES (?)";
    private static final String DELETE_RATING = "DELETE FROM MPA_RATINGS WHERE id = ?";
    static String GET_FILM_MPA = """
            SELECT r.* FROM MPA_RATINGS r
            JOIN FILMS f ON r.id = f.mpa_id
            WHERE f.id = ?
            ORDER BY r.id ASC
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
}

