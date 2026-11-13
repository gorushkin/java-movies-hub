package ru.practicum.moviehub.http;

import com.google.gson.JsonElement;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.exception.MovieValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    private static final int minYear = 1888;
    private static final int maxYear = LocalDate.now().getYear() + 1;
    public static void validateMovie(MovieDTO movieDTO) throws MovieValidationException {
        List<String> errors = new ArrayList<>();

        var title = movieDTO.title();
        var year = movieDTO.year();

        validateYear(year, errors);
        validateTitle(title, errors);

        if (!errors.isEmpty()) {
            throw new MovieValidationException(errors);
        }
    }

    private static void validateYear(Integer year, List<String> errors) {


        if (year == null) {
            errors.add("год должен быть указан");
        } else if (year < minYear || year > maxYear) {
            errors.add(String.format("год должен быть между %s и %s", minYear, maxYear));
        }
    }

    private static void validateTitle(String title, List<String> errors) {
        int maxTitleLength = 100;

        if (title == null || title.trim().isEmpty()) {
            errors.add("название не должно быть пустым");
        }

        if (title != null && title.length() > maxTitleLength) {
            errors.add(String.format("максимальная длина названия - %s символов", maxTitleLength));
        }
    }

    public static void validateYearConversion(JsonElement year) {
        if (year == null || year.isJsonNull() || !year.isJsonPrimitive()) {
            return;
        }

        var value = year.getAsJsonPrimitive();

        if (!value.isNumber() && !value.isString()) {
            return;
        }

        try {
            var numericYear = value.getAsBigDecimal();
            if (numericYear.stripTrailingZeros().scale() > 0) {
                throw new MovieValidationException(List.of("год должен быть целым числом"));
            }
            numericYear.intValueExact();
        } catch (ArithmeticException e) {
            throw new MovieValidationException(
                    List.of(String.format("год должен быть между %s и %s",
                            minYear, maxYear)));
        } catch (NumberFormatException e) {
            throw new BadRequestException("год должен быть числом");
        }
    }
}
