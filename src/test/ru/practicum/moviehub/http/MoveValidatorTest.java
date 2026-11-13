package ru.practicum.moviehub.http;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.dto.ErrorResponse;
import ru.practicum.moviehub.exception.MovieValidationException;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MoveValidatorTest {
    @Test
    void validateYearConversion_whenYearOverflowsInteger_reportsAllowedRange() {
        var exception = assertThrows(MovieValidationException.class,
                () -> MoveValidator.validateYearConversion(new JsonPrimitive(new BigDecimal("4294969296"))));

        var error = (ErrorResponse) exception.getResponseBody();
        assertEquals(List.of("год должен быть между 1888 и " + (LocalDate.now().getYear() + 1)),
                error.getDetails());
    }

    @Test
    void validateYearConversion_whenYearIsFractional_reportsIntegerRequirement() {
        var exception = assertThrows(MovieValidationException.class,
                () -> MoveValidator.validateYearConversion(new JsonPrimitive(new BigDecimal("2000.5"))));

        var error = (ErrorResponse) exception.getResponseBody();
        assertEquals(List.of("год должен быть целым числом"), error.getDetails());
    }

    @Test
    void validateMovie_whenYearIsOutsideAllowedRange_throwsValidationException() {
        int maxYear = LocalDate.now().getYear() + 1;

        for (int year : new int[]{1887, maxYear + 1}) {
            var movie = new MovieDTO(year, "Movie");
            var exception = assertThrows(MovieValidationException.class,
                    () -> MoveValidator.validateMovie(movie), "Год: " + year);
            assertEquals(422, exception.getResponseCode());
        }
    }

    @Test
    void validateMovie_whenYearIsAtAllowedBoundary_acceptsMovie() {
        int maxYear = LocalDate.now().getYear() + 1;

        for (int year : new int[]{1888, maxYear}) {
            assertDoesNotThrow(() -> MoveValidator.validateMovie(new MovieDTO(year, "Movie")),
                    "Год: " + year);
        }
    }

    @Test
    void validateMovie_whenTitleIsNullEmptyOrBlank_throwsValidationException() {
        for (String title : new String[]{null, "", " "}) {
            var movie = new MovieDTO(2000, title);
            assertThrows(MovieValidationException.class, () -> MoveValidator.validateMovie(movie),
                    "Название: " + title);
        }
    }

    @Test
    void validateMovie_whenTitleHas101Characters_throwsValidationException() {
        var movie = new MovieDTO(2000, "a".repeat(101));

        assertThrows(MovieValidationException.class, () -> MoveValidator.validateMovie(movie));
    }

    @Test
    void validateMovie_whenTitleHas100Characters_acceptsMovie() {
        var movie = new MovieDTO(2000, "a".repeat(100));

        assertDoesNotThrow(() -> MoveValidator.validateMovie(movie));
    }
}
