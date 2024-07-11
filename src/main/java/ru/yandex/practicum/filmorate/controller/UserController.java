package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Проверка условий на создание пользователя");
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(Level.WARN);
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            log.error("Ошибка при заполнении email");
            throw new ValidationException("Имейл должен быть указан и содержать символ @");
        }
        if (user.getLogin() == null && user.getLogin().isBlank()) {
            log.error("Ошибка при заполнении login");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            log.info("Если имя пустое, то присваиваем логин");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при заполнении birthday");
            throw new ValidationException("Дата рождения не может быть в будущем");

        }

        log.info("Создаем нового пользователя!");
        user.setId(getNextId());
        user.setEmail(user.getEmail());
        user.setLogin(user.getLogin());
        user.setName(user.getName());
        user.setBirthday(user.getBirthday());

        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен!");
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Проверка условий на обновление пользователя");
        if (newUser.getId() == null) {
            log.error("Пустой id");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                log.error("Ошибка при обновлении email: не указан");
                throw new ValidationException("Имейл должен быть указан");
            }

            if (newUser.getId() == null && newUser.getEmail() == null && newUser.getName() == null
                    || newUser.getLogin() == null || newUser.getBirthday() == null) {
                log.info("Новый пользователь соответствует уже имеющемуся");
                newUser = oldUser;
            }

            log.info("Обновляем пользователя");
            oldUser.setEmail(newUser.getEmail());
            oldUser.setName(newUser.getName());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь обновлен!");
            return oldUser;
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}