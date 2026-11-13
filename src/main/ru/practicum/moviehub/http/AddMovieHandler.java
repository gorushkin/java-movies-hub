package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.exception.UnsupportedMediaTypeException;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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

        MovieDTO movieDTO = parseMovieJson(gson, movieDTODeserialized);
        MoveValidator.validateMovie(movieDTO);

        Movie addedMovie = this.moviesStore.addMovie(movieDTO.year(), movieDTO.title());
        sendResponseWithBody(addedMovie, exchange, 201);
    }

    private MovieDTO parseMovieJson(Gson gson, String body) {
        try (JsonReader reader = new JsonReader(new StringReader(body))) {

            reader.setLenient(false);
            JsonElement json = gson.getAdapter(JsonElement.class).read(reader);

            if (json == null || !json.isJsonObject() || reader.peek() != JsonToken.END_DOCUMENT) {
                throw new BadRequestException("Тело запроса должно содержать объект фильма");
            }

            return gson.fromJson(json, MovieDTO.class);
        } catch (IOException | JsonSyntaxException e) {
            throw new BadRequestException("Тело запроса должно содержать корректный JSON");
        }
    }
}
