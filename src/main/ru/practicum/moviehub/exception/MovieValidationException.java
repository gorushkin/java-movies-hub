package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

import java.util.List;

public class MovieValidationException extends AppException {
    public static String errorName = "Ошибка валидации";

    public MovieValidationException(List<String> errors) {
        super(422, new ErrorResponse(errorName, errors));
    }
}

