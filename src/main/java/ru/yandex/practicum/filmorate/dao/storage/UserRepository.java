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
}
