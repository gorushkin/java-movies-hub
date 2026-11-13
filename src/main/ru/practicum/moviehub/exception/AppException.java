package ru.practicum.moviehub.exception;

public class AppException extends RuntimeException {
    private final int responseCode;
    private Object responseBody = null;

    public AppException(int responseCode, Object responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public AppException(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Object getResponseBody() {
        return responseBody;
    }
}
