package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeRepository {
    private static final String ADD_LIKE_QUERY = "INSERT INTO LIKES (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKE_COUNT_QUERY = "SELECT COUNT(user_id) FROM LIKES WHERE film_id = ?";

    JdbcTemplate jdbc;

    public LikeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Добавление лайка: filmId={}, userId={}", filmId, userId);
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаление лайка: filmId={}, userId={}", filmId, userId);
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public int getLikeCount(Long filmId) {
        log.debug("Получение количества лайков для фильма с id={}", filmId);
        Integer likeCount = jdbc.queryForObject(GET_LIKE_COUNT_QUERY, Integer.class, filmId);
        return likeCount != null ? likeCount : 0;

    }
}
