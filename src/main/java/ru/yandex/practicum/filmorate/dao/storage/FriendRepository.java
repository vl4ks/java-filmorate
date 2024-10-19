package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendRepository {
    private static final String ADD_FRIEND_QUERY = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
    private static final String GET_USER_FRIENDS_QUERY = "SELECT friend_id FROM FRIENDS WHERE user_id = ?";
    private static final String GET_COMMON_FRIENDS_QUERY = """
        SELECT friend_id
        FROM FRIENDS
        WHERE user_id = ?
        AND friend_id IN (SELECT friend_id FROM FRIENDS WHERE user_id = ?)
    """;

    private final JdbcTemplate jdbc;

    public FriendRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга: userId={}, friendId={}", userId, friendId);
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаление друга: userId={}, friendId={}", userId, friendId);
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    public Collection<Long> getUserFriends(Long userId) {
        log.debug("Получение списка друзей пользователя с id={}", userId);
        return jdbc.queryForList(GET_USER_FRIENDS_QUERY, Long.class, userId);
    }

    public Collection<Long> getCommonFriends(Long userId, Long otherId) {
        log.debug("Получение общих друзей для пользователей с id={} и {}", userId, otherId);
        return jdbc.queryForList(GET_COMMON_FRIENDS_QUERY, Long.class, userId, otherId);
    }
}

