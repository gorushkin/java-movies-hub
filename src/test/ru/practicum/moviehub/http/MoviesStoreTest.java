package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.store.MoviesStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoviesStoreTest {
    private List<MovieDTO> getMoviesData(int moviesCount) {
        List<MovieDTO> movieData = new ArrayList<>();

        for (int i = 0; i < moviesCount; i++) {
            var movie = new MovieDTO(LocalDate.now().getYear() - moviesCount + i, "movieTitle" + i);
            movieData.add(movie);
        }

        return movieData;
    }

    private List<MovieDTO> getMoviesData(int moviesCount, int year) {
        List<MovieDTO> movieData = new ArrayList<>();

        for (int i = 0; i < moviesCount; i++) {
            var movie = new MovieDTO(year, "movieTitle" + i);
            movieData.add(movie);
        }

        return movieData;
    }

    @Test
    public void shouldCreatEmptyStore() {
        MoviesStore store = new MoviesStore();
        Assertions.assertNotNull(store);
        Assertions.assertEquals(0, store.size());
    }

    @Test
    public void shouldAddMovieToStore() {
        MoviesStore store = new MoviesStore();

        var moviesSize = 3;
        List<MovieDTO> movieDTOList = getMoviesData(moviesSize);

        List<Movie> createdMovies = new ArrayList<>();

        for (var movieData : movieDTOList) {
            var createdMovie = store.addMovie(movieData.year(), movieData.title());
            createdMovies.add(createdMovie);
        }

        Assertions.assertEquals(movieDTOList.size(), store.size());
        Assertions.assertEquals(createdMovies, store.getAllMovies());
    }

    @Test
    public void shouldReturnMovieById() {
        MoviesStore store = new MoviesStore();

        var movie1 = store.addMovie(LocalDate.now().getYear(), "movieTitle1");
        var movie2 = store.addMovie(LocalDate.now().getYear(), "movieTitle1");

        var retrievedMovie1 = store.getMovieById(movie1.getId());
        var retrievedMovie2 = store.getMovieById(movie2.getId());

        Assertions.assertEquals(movie1, retrievedMovie1);
        Assertions.assertEquals(movie2, retrievedMovie2);
    }


    @Test
    public void shouldFilterMoviesByYear() {
        MoviesStore store = new MoviesStore();

        var targetYear = LocalDate.now().getYear() - 10;
        var expectedMoviesCount = 3;

        var otherYear1 = targetYear - 5;
        var otherYear2 = targetYear - 4;
        var otherYear3 = targetYear + 5;
        var otherYear4 = targetYear + 4;

        Integer[] nonTargetYears = {otherYear1, otherYear2, otherYear3, otherYear4};

        List<MovieDTO> movieDTOList = getMoviesData(expectedMoviesCount, targetYear);

        for (int year : nonTargetYears) {
            store.addMovie(year, "movieTitle" + year);
        }

        List<Movie> createdMovies = new ArrayList<>();

        for (var movieData : movieDTOList) {
            createdMovies.add(store.addMovie(movieData.year(), movieData.title()));
        }

        var filteredMovies = store.getMoviesByYear(targetYear);
        Assertions.assertEquals(expectedMoviesCount, filteredMovies.size());
        Assertions.assertEquals(createdMovies, filteredMovies);
    }

    @Test
    public void shouldDeleteMovieById() {
        MoviesStore store = new MoviesStore();

        var movieIdToDelete = 1;

        List<MovieDTO> movieDTOList = getMoviesData(5);
        List<Movie> createdMovies = new ArrayList<>();

        for (var movieData : movieDTOList) {
            createdMovies.add(store.addMovie(movieData.year(), movieData.title()));
        }

        store.deleteMovie(movieIdToDelete);
        Assertions.assertEquals(createdMovies.size() - 1, store.size());

        var deletedMovie = store.getMovieById(movieIdToDelete);
        Assertions.assertNull(deletedMovie);
    }
}
