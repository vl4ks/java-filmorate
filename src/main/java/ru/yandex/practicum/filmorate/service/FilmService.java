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
    FilmRepository filmRepository;
    MpaRatingRepository mpaRatingRepository;
    GenreRepository genreRepository;
    LikeRepository likeRepository;

    public Collection<FilmDto> getAllFilms() {
        Collection<Film> films = filmRepository.findAllWithRatingsAndLikes();

        Map<Long, Set<Genre>> filmGenres = genreRepository.findGenresByFilmIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet()));

        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, film.getMpa(),
                        filmGenres.getOrDefault(film.getId(), Collections.emptySet()), film.getLikesCount()))
                .collect(Collectors.toList());
    }


    public FilmDto getFilmById(long filmId) {
        Film film = filmRepository.findById(filmId);
        MpaRating mpaRating = mpaRatingRepository.findRatingByFilmId(filmId);
        Set<Genre> genres = genreRepository.findGenresByFilmId(filmId);

        return FilmMapper.mapToFilmDto(film, mpaRating, genres, film.getLikesCount());
    }


    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        if (newFilmRequest.getReleaseDate() == null) {
            throw new ParameterNotValidException("Дата выхода фильма должна быть указана");
        }
        if (newFilmRequest.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || newFilmRequest.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ParameterNotValidException("Дата выхода фильма не может быть в будущем и ранее 1895-12-28");
        }

        if (newFilmRequest.getMpa() == null || newFilmRequest.getMpa().getId() == null
                || newFilmRequest.getMpa().getId() < 1 || newFilmRequest.getMpa().getId() > 5) {
            throw new ParameterNotValidException("Рейтинг не должен быть равным null и id должно быть от 1 до 5");
        }

        Film film = filmRepository.create(newFilmRequest);


        if (newFilmRequest.getGenres() != null && !newFilmRequest.getGenres().isEmpty()) {
            genreRepository.createGenreFilmRelation(film.getId(), newFilmRequest.getGenres());
        }

        Set<Genre> genres = genreRepository.findGenresByFilmId(film.getId());

        return FilmMapper.mapToFilmDto(film, film.getMpa(), genres, film.getLikesCount());
    }


    public FilmDto updateFilm(FilmDto newFilm) {
        Film film = filmRepository.update(newFilm);

        if (!(newFilm.getGenres() == null) && !newFilm.getGenres().isEmpty()) {
            genreRepository.createGenreFilmRelation(film.getId(), newFilm.getGenres());
        }

        return FilmMapper.mapToFilmDto(
                film,
                mpaRatingRepository.findRatingByFilmId(film.getId()),
                genreRepository.findGenresByFilmId(film.getId()),
                film.getLikesCount()
        );
    }

    public FilmDto addLike(long filmId, long userId) {
        likeRepository.addLike(filmId, userId);
        return FilmMapper.mapToFilmDto(filmRepository.findById(filmId),
                mpaRatingRepository.findRatingByFilmId(filmId),
                genreRepository.findGenresByFilmId(filmId),
                likeRepository.getLikeCount(filmId));
    }

    public void removeLike(long filmId, long userId) {
        likeRepository.removeLike(filmId, userId);
    }

    public Collection<FilmDto> getPopularFilms(int count) {
        return filmRepository.getPopularFilms(count).stream()
                .map(film -> FilmMapper.mapToFilmDto(film, film.getMpa(),
                        genreRepository.findGenresByFilmId(film.getId()),
                        film.getLikesCount()))
                .collect(Collectors.toList());
    }
}