package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.debug("Создание нового пользователя: {}", user);
        return userService.createUser(user);
    }


    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.debug("Обновление пользователя {}", newUser);
        return userService.updateUser(newUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestBody User user) {
        log.debug("Удаление пользователя: ", user);
        userService.delete(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.debug("Добавление друга с id={} для пользователя с id={}.", friendId, id);
        return userService.addFriend(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.debug("Удаление друга с id={} у пользователя с id={}.", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable("id") Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        log.debug("Получение списка общих друзей пользователей с id={} и id={}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
