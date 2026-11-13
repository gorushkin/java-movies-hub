package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class NotAllowedMethod extends AppException {
    public NotAllowedMethod(String message) {
        super(405, new ErrorResponse(message));
    }
}
