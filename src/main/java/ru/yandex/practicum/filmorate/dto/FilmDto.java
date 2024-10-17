package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {
    private Long id;
    @NotBlank(message = "Название фильма не должно быть пустым.")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    private String description;
    @PastOrPresent(message = "Дата выхода фильма должна быть в прошлом или настоящем.")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Long duration;
    @NotNull(message = "Рейтинг не должен быть пустым")
    private MpaRating mpa;
    private Set<Genre> genres;
    private Set<Long> likes;
}


