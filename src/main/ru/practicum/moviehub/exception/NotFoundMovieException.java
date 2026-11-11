package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class NotFoundMovieException extends AppException {
    public static String errorName = "Фильм не найден";

    public NotFoundMovieException() {
        super(404, new ErrorResponse(errorName));
    }
}
