package ru.practicum.moviehub.model;

import ru.practicum.moviehub.exception.MovieValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Movie {
    private final String title;
    private final int year;
    private final int id;

    public Movie(int id, int year, String title) {
        String trimmedTitle = title.trim();

        validateMovie(year, title);

        this.id = id;
        this.year = year;
        this.title = trimmedTitle;
    }

    private void validateMovie(int year, String title) {
        List<String> errors = new ArrayList<>();

        validateYear(year, errors);
        validateTitle(title, errors);

        if (!errors.isEmpty()) {
            throw new MovieValidationException(errors);
        }
    }

    private void validateYear(int year, List<String> errors) {
        int minYear = 1900;
        int maxYear = LocalDate.now().getYear() + 1;

        if (year < minYear || year > maxYear) {
            errors.add(String.format("год должен быть между %s и %s", minYear, maxYear));
        }
    }

    private void validateTitle(String title, List<String> errors) {
        int maxTitleLength = 100;

        if (title.isEmpty()) {
            errors.add("название не должно быть пустым");
        }

        if (title.length() > maxTitleLength) {
            errors.add(String.format("максимальная длина названия - %s символов", maxTitleLength));
        }
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return year == movie.year && id == movie.id && Objects.equals(title, movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year, id);
    }
}