package ru.practicum.moviehub.exception;

public class UnsupportedMediaTypeException extends AppException {
    public UnsupportedMediaTypeException() {
        super(415, "Unsupported Media Type");
    }
}
