package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE id = ?";
    private static final String CREATE_USER_QUERY = "INSERT INTO USERS (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM USERS WHERE id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO FRIENDS (user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM FRIENDS WHERE user_id = ? " +
            "AND friend_id = ?";
    private static final String GET_USER_FRIENDS_QUERY = "SELECT u.* FROM USERS u " +
            "JOIN FRIENDS f ON u.id = f.friend_id WHERE f.user_id = ?";
    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT u.* FROM USERS u " +
            "JOIN FRIENDS f1 ON u.id = f1.friend_id JOIN FRIENDS f2 ON u.id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    public UserRepository(JdbcTemplate jdbc) {
        super(jdbc, new UserRowMapper());
    }

    public Collection<User> findAll() {
        log.debug("Получение всех пользователей");
        return findMany(FIND_ALL_QUERY);
    }

    public User findById(Long id) {
        log.debug("Поиск пользователя с id={}", id);
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    public User create(User user) {
        log.debug("Создание пользователя: {}", user);
        long id;
        if (user.getName() == null) {
            id = insert(CREATE_USER_QUERY, user.getLogin(), user.getLogin(), user.getEmail(),
                    user.getBirthday().toString());
        } else {
            id = insert(CREATE_USER_QUERY, user.getName(), user.getLogin(), user.getEmail(),
                    user.getBirthday().toString());
        }
        return findById(id);
    }

    public User update(User newUser) {
        log.debug("Обновление пользователя: {}", newUser);
        findById(newUser.getId());
        update(UPDATE_USER_QUERY, newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday(), newUser.getId());
        return findById(newUser.getId());
    }

    public void delete(User user) {
        log.debug("Удаление пользователя: {}", user);
        if (user.getId() != null) {
            update(DELETE_USER_QUERY, user.getId());
        }
    }

    public User addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга: userId={}, friendId={}", userId, friendId);
        findById(userId);
        findById(friendId);
        update(ADD_FRIEND_QUERY, userId, friendId);
        log.debug("Друг добавлен: userId={}, friendId={}", userId, friendId);
        return findById(userId);
    }


    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаление друга: userId={}, friendId={}", userId, friendId);
        findById(userId);
        findById(friendId);
        update(REMOVE_FRIEND_QUERY, userId, friendId);
        log.debug("Друг удален: userId={}, friendId={}", userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Получение общих друзей для пользователей с id= {} и {}", userId, otherId);
        findById(userId);
        findById(otherId);
        return findMany(GET_COMMON_FRIENDS_QUERY, userId, otherId).stream().toList();
    }

    public Collection<User> getUserFriends(Long userId) {
        log.debug("Получение друзей пользователя с id= {}", userId);
        return findMany(GET_USER_FRIENDS_QUERY, userId).stream().toList();
    }
}
