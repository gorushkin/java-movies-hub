package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class BadRequestException extends AppException {
    public static String errorName = "Некорректный ID";

    public BadRequestException() {
        super(400, new ErrorResponse(errorName));
    }
}
