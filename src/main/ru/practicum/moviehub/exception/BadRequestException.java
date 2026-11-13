package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(400, new ErrorResponse(message));
    }
}