package ru.yandex.practicum.filmorate.storage;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Проверка условий на создание пользователя");
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(Level.WARN);
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            log.error("Ошибка при заполнении email");
            throw new ValidationException("Имейл должен быть указан и содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Ошибка при заполнении login");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            log.info("Если имя пустое, то присваиваем логин");
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при заполнении birthday");
            throw new ValidationException("Дата рождения не может быть в будущем");

        }

        log.info("Создаем нового пользователя!");
        user.setId(idCounter++);
        user.setEmail(user.getEmail());
        user.setLogin(user.getLogin());
        user.setBirthday(user.getBirthday());

        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен!");
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Проверка условий на обновление пользователя");
        if (newUser.getId() == null) {
            log.error("Пустой id");
            throw new ValidationException("Id должен быть указан");
        }

        log.info("Проверка наличия пользователя");
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            log.info("Проверка и обновление email");
            if (newUser.getEmail() != null) {
                if (newUser.getEmail().isBlank()) {
                    log.error("Ошибка при обновлении email: не указан");
                    throw new ValidationException("Имейл должен быть указан");
                }
                oldUser.setEmail(newUser.getEmail());
            }

            log.info("Проверка и обновление login");
            if (newUser.getLogin() != null) {
                if (newUser.getLogin().isBlank()) {
                    log.error("Ошибка при обновлении login");
                    throw new ValidationException("Логин не может содержать пробелы");
                }
                oldUser.setLogin(newUser.getLogin());
            }

            log.info("Проверка и обновление name");
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }

            log.info("Проверка и обновление birthday");
            if (newUser.getBirthday() != null) {
                if (newUser.getBirthday().isAfter(LocalDate.now())) {
                    log.error("Ошибка при обновлении birthday");
                    throw new ValidationException("Дата рождения не может быть в будущем");
                }
                oldUser.setBirthday(newUser.getBirthday());
            }

            log.info("Пользователь обновлен!");
            return oldUser;
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
