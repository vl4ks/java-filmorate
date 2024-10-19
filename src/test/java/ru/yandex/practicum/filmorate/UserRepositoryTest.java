package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.storage.FriendRepository;
import ru.yandex.practicum.filmorate.dao.storage.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@Import({UserRepository.class, FriendRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("UserRepository")
class UserRepositoryTest {
    public static final long TEST_USER_ID = 1L;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    static User getTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail("user1@example.com");
        user.setLogin("user1login");
        user.setName("User One");
        user.setBirthday(LocalDate.of(1985, 01, 01));
        return user;
    }

    @Test
    @DisplayName("должен находить пользователя по id")
    public void should_return_user_when_find_by_id() {
        User user = userRepository.findById(TEST_USER_ID);
        assertThat(user)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("должен создавать нового пользователя")
    public void should_create_new_user() {
        User newUser = new User();
        newUser.setLogin("newlogin");
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        newUser.setBirthday(LocalDate.of(2000, 4, 15));

        User createdUser = userRepository.create(newUser);

        assertThat(createdUser).isNotNull();

        assertThat(createdUser)
                .usingRecursiveComparison()
                .ignoringFields("id", "friends")
                .isEqualTo(newUser);
    }

    @Test
    @DisplayName("должен удалять пользователя")
    public void should_delete_user_by_id() {
        userRepository.delete(getTestUser());
        Collection<User> users = userRepository.findAll();
        assertThat(users).extracting(User::getId).doesNotContain(1L);
    }


    @Test
    @DisplayName("должен возвращать всех пользователей")
    public void should_return_all_users() {
        Collection<User> users = userRepository.findAll();
        assertThat(users).hasSize(3);
    }

    @Test
    @DisplayName("должен добавлять друга пользователю")
    public void should_add_friend() {
        friendRepository.addFriend(2L, 1L);

        Collection<Long> friends = friendRepository.getUserFriends(2L);
        assertThat(friends).contains(1L);
    }

    @Test
    @DisplayName("должен удалять друга пользователя")
    public void should_remove_friend() {
        friendRepository.removeFriend(2L, 1L);

        Collection<Long> friends = friendRepository.getUserFriends(2L);
        assertThat(friends).doesNotContain(1L);
        assertThat(friends).contains(3L);
    }

    @Test
    @DisplayName("должен возвращать список друзей пользователя")
    public void should_return_user_friends() {
        Collection<Long> friends = friendRepository.getUserFriends(1L);

        assertThat(friends).hasSize(2);
        List<String> friendNames = friends.stream()
                .map(friendId -> userRepository.findById(friendId).getName())
                .collect(Collectors.toList());
        assertThat(friendNames).containsExactlyInAnyOrder("User Two", "User Three");
    }

    @Test
    @DisplayName("должен возвращать список общих друзей")
    public void should_return_common_friends() {
        Collection<Long> commonFriends = friendRepository.getCommonFriends(1L, 2L);

        assertThat(commonFriends).hasSize(1);
        List<String> commonFriendNames = commonFriends.stream()
                .map(friendId -> userRepository.findById(friendId).getName())
                .collect(Collectors.toList());
        assertThat(commonFriendNames).contains("User Three");
    }
}

