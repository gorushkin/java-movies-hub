package ru.practicum.moviehub.model;

import java.time.LocalDate;

public class Movie {
    private final String title;
    private final int year;
    private final int id;

    public Movie(int id, int year, String title) {
        validateYear(year);

        String trimmedTitle = title.trim();

        validateTitle(trimmedTitle);

        this.id = id;
        this.year = year;
        this.title = trimmedTitle;
    }

    private void validateYear(int year) {
        int minYear = 1900;
        int maxYear = LocalDate.now().getYear() + 1;

        if (year < minYear || year > maxYear) {
            throw new MovieValidationException(String.format("год должен быть между %s и %s", minYear, maxYear));
        }
    }

    private void validateTitle(String title) {
        int maxTitleLength = 100;

        if (title.isEmpty()) {
            throw new MovieValidationException("название не должно быть пустым");
        }

        if (title.length() > maxTitleLength) {
            throw new MovieValidationException(String.format("максимальная длина названия - %s символов", maxTitleLength));
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
}