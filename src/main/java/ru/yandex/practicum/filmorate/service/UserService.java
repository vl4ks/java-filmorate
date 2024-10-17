package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.create(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId);
    }

    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User newUser) {
        return userRepository.update(newUser);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User addFriend(Long userId, Long friendId) {
        return userRepository.addFriend(userId, friendId);
    }

    public Collection<User> getUserFriends(Long userId) {
        userRepository.findById(userId);
        return userRepository.getUserFriends(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userRepository.removeFriend(userId, friendId);
    }


    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        return userRepository.getCommonFriends(userId, otherId);
    }
}