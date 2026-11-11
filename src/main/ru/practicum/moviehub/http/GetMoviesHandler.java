package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class GetMoviesHandler extends BaseHttpHandlerWithStore {
    public GetMoviesHandler(MoviesStore moviesStore) {
        super(moviesStore);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            var query = getQuery("year", exchange);

            var movies = query == null ?
                    moviesStore.getMovies() :
                    moviesStore.getMovies(Integer.parseInt(query));

            sendResponse(movies, exchange, 200);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректный параметр запроса — 'year'");
        }
    }
}

