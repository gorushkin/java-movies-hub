package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesStore {
    Map<Integer, Movie> movies = new HashMap<>();

    private int currentIndex = 1;

    public Movie addMovie(int year, String title) {
        Movie movie = new Movie(currentIndex, year, title);
        movies.put(currentIndex, movie);
        currentIndex++;
        return movie;
    }

    public Movie getMovieById(int id) {
        return movies.get(id);
    }

    public boolean deleteMovie(int id) {
        if (!movies.containsKey(id)) {
            return false;
        }

        this.movies.remove(id);
        return true;
    }

    public int size() {
        return movies.size();
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movies.values());
    }

    public List<Movie> getMovies(int year) {
        return new ArrayList<>(movies.values()).stream().filter((m) -> m.getYear() == year).toList();
    }
}