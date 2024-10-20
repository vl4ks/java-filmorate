package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {
    private final MpaRatingService mpaRatingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<MpaRating> getMpaRatings() {
        return mpaRatingService.getAllMpaRatings();
    }

    @GetMapping("/{ratingId}")
    public MpaRating getMpaRatingById(@PathVariable("ratingId") long ratingId) {
        return mpaRatingService.getRatingById(ratingId);
    }

    @PostMapping
    public MpaRating create(@RequestBody MpaRatingDto newMpaRatingDto) {
        return mpaRatingService.createMpaRating(newMpaRatingDto);
    }

    @DeleteMapping("/{ratingId}")
    public void delete(@PathVariable @RequestParam Long id) {
        log.debug("Удаление рейтинга с id: {}", id);
        mpaRatingService.delete(id);
    }
}