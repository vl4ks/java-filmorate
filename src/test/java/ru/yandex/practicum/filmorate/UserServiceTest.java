package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    public void testAddFriend() {
        User user1 = User.builder()
                .id(1L)
                .email("mail1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(2000, 1, 2))
                .build();
        userStorage.create(user1);
        userStorage.create(user2);

        userService.addFriend(1L, 2L);

        assertTrue(userStorage.findById(1L).get().getFriends().contains(2L));
        assertTrue(userStorage.findById(2L).get().getFriends().contains(1L));
    }

    @Test
    public void testRemoveFriend() {
        User user1 = User.builder()
                .id(1L)
                .email("mail1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(2000, 1, 2))
                .build();
        userStorage.create(user1);
        userStorage.create(user2);
        userService.addFriend(1L, 2L);

        userService.removeFriend(1L, 2L);

        assertFalse(userStorage.findById(1L).get().getFriends().contains(2L));
        assertFalse(userStorage.findById(2L).get().getFriends().contains(1L));
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = User.builder()
                .id(1L)
                .email("mail1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(2000, 1, 2))
                .build();
        User user3 = User.builder()
                .id(3L)
                .email("mail3@mail.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(2000, 1, 3))
                .build();
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
        userService.addFriend(1L, 3L);
        userService.addFriend(2L, 3L);

        List<User> commonFriends = userService.getCommonFriends(1L, 2L);

        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(user3));
    }
}
