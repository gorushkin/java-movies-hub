package ru.practicum.moviehub.model;

public class MovieValidationException extends RuntimeException {
    public MovieValidationException(String message) {
        super(message);
    }
}
