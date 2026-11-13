package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.exception.UnsupportedMediaTypeException;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AddMovieHandler extends BaseHttpHandlerWithStore {
    public AddMovieHandler(MoviesStore moviesStore) {
        super(moviesStore);
    }

    private void validateRequest(HttpExchange exchange) {
        String contentType = exchange.getRequestHeaders()
                .getFirst("Content-Type");

        if (contentType == null ||
                !contentType.split(";", 2)[0].trim().equalsIgnoreCase("application/json")) {
            throw new UnsupportedMediaTypeException();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        validateRequest(exchange);

        Gson gson = new Gson();
        InputStream inputStream = exchange.getRequestBody();
        String movieDTODeserialized = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            MovieDTO movieDTO = gson.fromJson(movieDTODeserialized, MovieDTO.class);

            if (movieDTO == null) {
                throw new BadRequestException("Тело запроса должно содержать объект фильма");
            }

            MoveValidator.validateMovie(movieDTO);

            Movie addedMovie = this.moviesStore.addMovie(movieDTO.year(), movieDTO.title());
            sendResponseWithBody(addedMovie, exchange, 201);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Тело запроса должно содержать объект фильма");
        }

    }
}
