package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.storage.FilmRepository;
import ru.yandex.practicum.filmorate.dao.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("FilmorateApplication")
public class FilmorateApplicationTests {
    @Autowired
    private UserRepository userStorage;

    @Autowired
    private FilmRepository filmStorage;

    @Test
    void contextLoads() {
        assertThat(userStorage).isNotNull();
        assertThat(filmStorage).isNotNull();
    }
}

