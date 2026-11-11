package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.exception.AppException;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class MoviesHandler extends BaseHttpHandler {
    public MoviesHandler(MoviesStore moviesStore) {
        super(moviesStore);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET" -> new GetMoviesHandler(this.moviesStore).handle(exchange);
                case "POST" -> new AddMovieHandler(this.moviesStore).handle(exchange);
                default -> exchange.sendResponseHeaders(405, -1);
            }
        } catch (AppException e) {
            sendResponse(e.getResponseBody(), exchange, e.getResponseCode());
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
