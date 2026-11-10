package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.model.MovieValidationException;

import java.time.LocalDate;

public class MovieTest {
    @Test
    void shouldCreateMovieWithYearAndTitle() {
        int year = LocalDate.now().getYear();
        var title = "title";
        var id = 1;

        Movie movie = new Movie(id, year, title);
        Assertions.assertEquals(year, movie.getYear());
        Assertions.assertEquals(title, movie.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenYearIsInvalid() {
        int properYear = LocalDate.now().getYear();
        int lessThanMinYear = 1000;
        int biggerThanMaxYear = LocalDate.now().getYear() + 10;

        String properTitle = "title";
        String toShortTitle = "";
        String toLongTitle = "0123456789".repeat(20);

        TestData[] testDataList = {
                new TestData(0, lessThanMinYear, properTitle),
                new TestData(1, biggerThanMaxYear, properTitle),
                new TestData(2, properYear, toShortTitle),
                new TestData(3, properYear, toLongTitle),
        };

        for (var testData : testDataList) {
            Assertions.assertThrows(MovieValidationException.class, () -> {
                new Movie(testData.id, testData.year, testData.title);
            });
        }
    }

    record TestData(int id, int year, String title) {
    }
}
