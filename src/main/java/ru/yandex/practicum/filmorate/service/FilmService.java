package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    private final FilmRepository filmRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final GenreRepository genreRepository;

    public Collection<FilmDto> getAllFilms() {
        return filmRepository.findAll()
                .stream()
                .map(film -> FilmMapper.mapToFilmDto(film, mpaRatingRepository.findRatingByFilmId(film.getId()),
                        genreRepository.findGenresByFilmId(film.getId()), filmRepository.getLikes(film.getId())))
                .collect(Collectors.toList());
    }


    public FilmDto getFilmById(long filmId) {
        return FilmMapper.mapToFilmDto(filmRepository.findById(filmId), mpaRatingRepository.findRatingByFilmId(filmId),
                genreRepository.findGenresByFilmId(filmId), filmRepository.getLikes(filmId));
    }


    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        Film film = filmRepository.create(newFilmRequest);

        if (newFilmRequest.getReleaseDate() == null) {
            throw new ParameterNotValidException("Дата выхода фильма должна быть указана");
        }
        if (newFilmRequest.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || newFilmRequest.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ParameterNotValidException("Дата выхода фильма не может быть в будущем и ранее 1895-12-28");
        }

        if (!(newFilmRequest.getMpa() == null || newFilmRequest.getMpa().getId() == null
                || newFilmRequest.getMpa().getId() < 1 || newFilmRequest.getMpa().getId() > 5)) {
            mpaRatingRepository.createMpaFilmRelation(film.getId(), newFilmRequest.getMpa().getId());
        } else {
            throw new ParameterNotValidException("Рейтинг не должен быть равным null и id должно быть от 1 до 5");
        }

        if (newFilmRequest.getGenres() != null && !newFilmRequest.getGenres().isEmpty()) {
            genreRepository.createGenreFilmRelation(film.getId(), newFilmRequest.getGenres());
        }

        MpaRating mpaRating = mpaRatingRepository.findRatingByFilmId(film.getId());
        Set<Genre> genres = genreRepository.findGenresByFilmId(film.getId());
        Set<Long> likes = filmRepository.getLikes(film.getId());

        return FilmMapper.mapToFilmDto(film, mpaRating, genres, likes);
    }


    public FilmDto updateFilm(FilmDto newFilm) {
        Film film = filmRepository.update(newFilm);

        if (!(newFilm.getMpa() == null) && newFilm.getMpa().getId() != 0) {
            mpaRatingRepository.createMpaFilmRelation(film.getId(), newFilm.getMpa().getId());
        }
        if (!(newFilm.getGenres() == null) && !newFilm.getGenres().isEmpty()) {
            genreRepository.createGenreFilmRelation(film.getId(), newFilm.getGenres());
        }

        return FilmMapper.mapToFilmDto(film, mpaRatingRepository.findRatingByFilmId(film.getId()),
                genreRepository.findGenresByFilmId(film.getId()), filmRepository.getLikes(film.getId()));
    }

    public FilmDto addLike(long filmId, long userId) {
        return FilmMapper.mapToFilmDto(filmRepository.addLike(filmId, userId),
                mpaRatingRepository.findRatingByFilmId(filmId),
                genreRepository.findGenresByFilmId(filmId), filmRepository.getLikes(filmId));
    }

    public void removeLike(long filmId, long userId) {
        filmRepository.removeLike(filmId, userId);
    }


    public Collection<FilmDto> getPopularFilms(int count) {
        return filmRepository.getPopularFilms(count).stream()
                .map(film -> FilmMapper.mapToFilmDto(film, mpaRatingRepository.findRatingByFilmId(film.getId()),
                        genreRepository.findGenresByFilmId(film.getId()), filmRepository.getLikes(film.getId())))
                .collect(Collectors.toList());
    }
}