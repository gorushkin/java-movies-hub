package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;

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
    void shouldCompareEqualsMovesCorrectly() {
        int year = LocalDate.now().getYear();
        String title = "title";

        int id = 1;
        Movie movie1 = new Movie(id, year, title);
        Movie movie2 = new Movie(id, year, title);

        Assertions.assertEquals(movie1, movie2);
    }


}
