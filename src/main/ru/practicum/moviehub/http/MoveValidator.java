package ru.practicum.moviehub.http;

import ru.practicum.moviehub.exception.MovieValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    public static void validateMovie(int year, String title) {
        List<String> errors = new ArrayList<>();

        validateYear(year, errors);
        validateTitle(title, errors);

        if (!errors.isEmpty()) {
            throw new MovieValidationException(errors);
        }
    }

    private static void validateYear(int year, List<String> errors) {
        int minYear = 1900;
        int maxYear = LocalDate.now().getYear() + 1;

        if (year < minYear || year > maxYear) {
            errors.add(String.format("год должен быть между %s и %s", minYear, maxYear));
        }
    }

    private static void validateTitle(String title, List<String> errors) {
        int maxTitleLength = 100;

        if (title == null || title.isEmpty()) {
            errors.add("название не должно быть пустым");
        }

        if (title != null && title.length() > maxTitleLength) {
            errors.add(String.format("максимальная длина названия - %s символов", maxTitleLength));
        }
    }
}
