package ru.practicum.moviehub.exception;

public class AppException extends RuntimeException {
    private final int responseCode;
    private final Object responseBody;

    public AppException(int responseCode, Object responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Object getResponseBody() {
        return responseBody;
    }
}
