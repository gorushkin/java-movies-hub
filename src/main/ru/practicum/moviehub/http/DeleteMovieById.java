package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.exception.NotFoundException;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class DeleteMovieById extends BaseHttpHandlerWithStore {
    public DeleteMovieById(MoviesStore moviesStore) {
        super(moviesStore);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] segments = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(segments[2]);

            var result = moviesStore.deleteMovie(id);

            if (!result) {
                throw new NotFoundException("Фильм не найден");
            }

            sendResponseWithoutBody( exchange, 204);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректный ID");
        }
    }
}
