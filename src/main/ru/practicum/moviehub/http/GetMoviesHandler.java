package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class GetMoviesHandler extends BaseHttpHandlerWithStore {
    public GetMoviesHandler(MoviesStore moviesStore) {
        super(moviesStore);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var movies = moviesStore.getAllMovies();

        sendResponse(movies, exchange, 200);
    }
}

