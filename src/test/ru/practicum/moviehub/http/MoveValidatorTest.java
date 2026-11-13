package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.MovieValidationException;

import java.time.LocalDate;

public class MoveValidatorTest {
    @Test
    void shouldThrowExceptionWhenYearIsInvalid() {
        int properYear = LocalDate.now().getYear();
        int lessThanMinYear = 1000;
        int biggerThanMaxYear = LocalDate.now().getYear() + 10;

        String properTitle = "title";
        String toShortTitle = " ";
        String toLongTitle = "0123456789".repeat(20);

        MovieDTO[] testDataList = {
                new MovieDTO(lessThanMinYear, properTitle),
                new MovieDTO(biggerThanMaxYear, properTitle),
                new MovieDTO(properYear, toShortTitle),
                new MovieDTO(properYear, toLongTitle),
        };

        for (var testData : testDataList) {
            Assertions.assertThrows(MovieValidationException.class, () -> {
                MoveValidator.validateMovie(testData.year(), testData.title());
            });
        }
    }
}
