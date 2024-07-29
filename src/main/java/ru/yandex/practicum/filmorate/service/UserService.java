package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
