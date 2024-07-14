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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends FilmorateApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
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
        User user = new User(null, "mail@mail.ru", "testuser", "Test User",
                LocalDate.of(1946, 8, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testCreateUserWithEmptyName() throws Exception {
        User user = new User(null, "mail@mail.ru", "testuser", null,
                LocalDate.of(1946, 8, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("testuser"));
    }

    @Test
    public void testCreateUserValidationFailure() {
        User user = new User(null, "invalidemail", "testuser", "Test User",
                LocalDate.of(1946, 8, 20));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertEquals("Имейл должен быть указан и содержать символ @", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        User user = new User(null, "mail@mail.ru", " ", "Test User",
                LocalDate.of(1946, 8, 20));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testCreateUserWithFutureBirthday() {
        User user = new User(null, "mail@mail.ru", "testuser", "Test User",
                LocalDate.of(2100, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }


    @Test
    public void testUpdateUser() throws Exception {
        User user = new User(1L, "test2@example.com", "testuser2", "Test User2",
                LocalDate.of(1946, 8, 20));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        User updatedUser = new User(1L, "test2@example.com", "updateduser", "Updated User",
                LocalDate.of(1946, 8, 20));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("updateduser"));
    }

    @Test
    public void testUpdateUserNotFound() {
        User updatedUser = new User(999L, "test2@example.com", "updateduser", "Updated User",
                LocalDate.of(1946, 8, 20));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.update(updatedUser);
        });

        assertEquals("Пользователь с id = 999 не найден", exception.getMessage());
    }
}

