package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.FriendRepository;
import ru.yandex.practicum.filmorate.dao.storage.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    FriendRepository friendRepository;

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
        userRepository.findById(userId);
        userRepository.findById(friendId);
        friendRepository.addFriend(userId, friendId);
        return userRepository.findById(userId);
    }

    public List<User> getUserFriends(Long userId) {
        userRepository.findById(userId);
        return friendRepository.getUserFriendsWithDetails(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userRepository.findById(userId);
        userRepository.findById(friendId);
        friendRepository.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        userRepository.findById(userId);
        userRepository.findById(otherId);
        return friendRepository.getCommonFriendsWithDetails(userId, otherId);

    }
}