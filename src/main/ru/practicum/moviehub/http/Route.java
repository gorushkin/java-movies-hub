package ru.practicum.moviehub.http;

import java.util.regex.Pattern;

public record Route(
        String method,
        Pattern path,
        BaseHttpHandler handler
) {
}