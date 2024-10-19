package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingStorage {

    Collection<MpaRating> findAll();

    MpaRating findById(Long id);

    MpaRating create(MpaRatingDto newMpaRatingDto);

    void delete(Long id);

    MpaRating findRatingByFilmId(Long filmId);
}
