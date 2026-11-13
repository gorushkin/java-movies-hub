package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class NotFoundRoute extends AppException {
    public NotFoundRoute(String message) {
        super(404, new ErrorResponse(message));
    }
}
