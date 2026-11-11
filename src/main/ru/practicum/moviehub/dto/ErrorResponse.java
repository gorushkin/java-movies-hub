package ru.practicum.moviehub.dto;

import java.util.List;

public record ErrorResponse(String error, List<String> details) {
}