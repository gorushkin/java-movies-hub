package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
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

        int id = Integer.parseInt(segments[2]);
        Movie movie = moviesStore.getMovieById(id);

        sendResponse(movie, exchange, 200);
    }
}
