package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends FilmorateApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;
    private UserService userService;
    private UserStorage userStorage;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    public void testFindAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = User.builder()
                .id(null)
                .email("mail@mail.ru")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testCreateUserWithEmptyName() throws Exception {
        User user = User.builder()
                .id(null)
                .email("mail@mail.ru")
                .login("testuser")
                .name(null)
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("testuser"));
    }

    @Test
    public void testCreateUserValidationFailure() {
        User user = User.builder()
                .id(null)
                .email("invalidemail")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertEquals("Имейл должен быть указан и содержать символ @", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        User user = User.builder()
                .id(null)
                .email("mail@mail.ru")
                .login("")
                .name("Test User")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testCreateUserWithFutureBirthday() {
        User user = User.builder()
                .id(null)
                .email("mail@mail.ru")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(2100, 1, 1))
                .build();
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }


    @Test
    public void testUpdateUser() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("test2@example.com")
                .login("testuser2")
                .name("Test User2")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        User updatedUser = User.builder()
                .id(1L)
                .email("test2@example.com")
                .login("updateduser")
                .name("Update User")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("updateduser"));
    }

    @Test
    public void testUpdateUserNotFound() {
        User updatedUser = User.builder()
                .id(999L)
                .email("test2@example.com")
                .login("updateduser")
                .name("Update User")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.update(updatedUser);
        });

        assertEquals("Пользователь с id = 999 не найден", exception.getMessage());
    }
}

