package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.exception.NotFoundException;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class GetMovieByIdHandler extends BaseHttpHandlerWithStore {
    public GetMovieByIdHandler(MoviesStore moviesStore) {
        super(moviesStore);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] segments = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(segments[2]);
            Movie movie = moviesStore.getMovieById(id);

            if (movie == null) {
                throw new NotFoundException("Фильм не найден");
            }

            sendResponseWithBody(movie, exchange, 200);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректный ID");
        }
    }
}
