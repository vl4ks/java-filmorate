package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.storage.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import(MpaRatingRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("MpaRatingRepository")
public class MpaRatingRepositoryTest {

    private final MpaRatingRepository mpaRatingRepository;

    private static final Long TEST_RATING_ID = 3L;

    static MpaRating getTestMpaRating() {
        return MpaRating.builder()
                .id(TEST_RATING_ID)
                .name("PG-13")
                .build();
    }


    @Test
    @DisplayName("должен возвращать все рейтинги")
    public void should_return_all_mpa_ratings() {
        Collection<MpaRating> ratings = mpaRatingRepository.findAll();
        assertThat(ratings).hasSize(3);
        assertThat(ratings).extracting("name")
                .containsExactlyInAnyOrder("G", "PG", "PG-13");
    }

    @Test
    @DisplayName("должен создавать новый рейтинг")
    public void should_create_new_mpa_rating() {
        MpaRatingDto newRating = new MpaRatingDto();
        newRating.setName("A");

        MpaRating createdRating = mpaRatingRepository.create(newRating);

        assertThat(createdRating.getId()).isNotNull();
        assertThat(createdRating.getName()).isEqualTo("A");
    }

    @Test
    @DisplayName("должен удалять рейтинг по id")
    public void should_delete_mpa_rating_by_id() {
        mpaRatingRepository.delete(TEST_RATING_ID);

        assertThrows(NotFoundException.class, () -> mpaRatingRepository.findById(TEST_RATING_ID));
    }

    @Test
    @DisplayName("должен создавать связь между фильмом и рейтингом")
    public void should_create_mpa_and_film() {
        Long newFilmId = 2L;
        Long newMpaId = 2L;

        mpaRatingRepository.createMpaFilmRelation(newFilmId, newMpaId);

        MpaRating mpa = mpaRatingRepository.findRatingByFilmId(newFilmId);
        assertThat(mpa.getName()).isEqualTo("PG");
    }
}
