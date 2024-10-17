package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    void delete(User user);

    User findById(Long id);

    User addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId, Long otherId);

    Collection<User> getUserFriends(Long userId);
}
