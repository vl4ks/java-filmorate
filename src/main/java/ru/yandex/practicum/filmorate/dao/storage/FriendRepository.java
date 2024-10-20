package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;


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
    private static final String GET_USER_FRIENDS_WITH_DETAILS_QUERY = """
        SELECT USERS.*
        FROM USERS
        JOIN FRIENDS ON USERS.id = FRIENDS.friend_id
        WHERE FRIENDS.user_id = ?
    """;

    private static final String GET_COMMON_FRIENDS_WITH_DETAILS_QUERY = """
        SELECT USERS.*
        FROM USERS
        JOIN FRIENDS f1 ON USERS.id = f1.friend_id
        JOIN FRIENDS f2 ON USERS.id = f2.friend_id
        WHERE f1.user_id = ? AND f2.user_id = ?
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

    public List<User> getUserFriendsWithDetails(Long userId) {
        return jdbc.query(GET_USER_FRIENDS_WITH_DETAILS_QUERY, new UserRowMapper(), userId);
    }

    public List<User> getCommonFriendsWithDetails(Long userId1, Long userId2) {
        return jdbc.query(GET_COMMON_FRIENDS_WITH_DETAILS_QUERY, new UserRowMapper(), userId1, userId2);
    }

    public Collection<Long> getCommonFriends(Long userId, Long otherId) {
        log.debug("Получение общих друзей для пользователей с id={} и {}", userId, otherId);
        return jdbc.queryForList(GET_COMMON_FRIENDS_QUERY, Long.class, userId, otherId);
    }
}

