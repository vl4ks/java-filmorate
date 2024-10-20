package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaRatingService {

    private final MpaRatingRepository mpaRatingRepository;

    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatingRepository.findAll();
    }

    public MpaRating getRatingById(long ratingId) {
        return mpaRatingRepository.findById(ratingId);
    }


    public MpaRating createMpaRating(MpaRatingDto newMpaRatingDto) {
        return mpaRatingRepository.create(newMpaRatingDto);
    }

    public void delete(Long id) {
        mpaRatingRepository.delete(id);
    }
}

