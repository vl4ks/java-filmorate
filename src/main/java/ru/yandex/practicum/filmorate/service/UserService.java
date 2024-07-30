package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        log.debug("Getting all users");
        return userStorage.findAll();
    }

    public User create(@Valid @RequestBody User user) {
        log.debug("Creating user: {}", user);
        return userStorage.create(user);
    }

    public User update(@Valid @RequestBody User newUser) {
        log.debug("Updating user: {}", newUser);
        return userStorage.update(newUser);
    }


    public void delete(@PathVariable Long id) {
        log.debug("Deleting user with id={}", id);
        userStorage.delete(id);
    }

    public Optional<User> findById(Long id) {
        log.debug("Searching user with id={}", id);
        return userStorage.findById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Adding friend: userId={}, friendId={}", userId, friendId);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.debug("Friend added: userId={}, friendId={}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Removing friend: userId={}, friendId={}", userId, friendId);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.debug("Friend removed: userId={}, friendId={}", userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Getting common friends: userId={}, otherId={}", userId, otherId);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User otherUser = userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherId + " не найден"));

        List<User> commonFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(id -> userStorage.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found")))
                .collect(Collectors.toList());
        log.debug("Found common friends: userId={}, otherId={}, commonFriends={}", userId, otherId, commonFriends);
        return commonFriends;
    }
}
