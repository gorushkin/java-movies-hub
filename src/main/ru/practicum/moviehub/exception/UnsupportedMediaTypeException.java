package ru.practicum.moviehub.exception;

import ru.practicum.moviehub.dto.ErrorResponse;

public class UnsupportedMediaTypeException extends AppException {
    public UnsupportedMediaTypeException() {
        super(400, new ErrorResponse("Unsupported Media Type"));
    }
}
