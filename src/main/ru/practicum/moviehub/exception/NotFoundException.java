package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(404, new ErrorResponse(message));
    }

    public NotFoundException() {
        super(404);
    }
}
