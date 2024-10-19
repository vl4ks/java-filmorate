package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не должно быть пустым.")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    private String description;
    @PastOrPresent(message = "Дата выхода фильма должна быть в прошлом или настоящем.")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Long duration;

    private MpaRating mpa;

    private int likesCount;
}