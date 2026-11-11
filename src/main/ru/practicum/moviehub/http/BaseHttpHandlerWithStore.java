package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.store.MoviesStore;

import java.util.Arrays;

public abstract class BaseHttpHandlerWithStore extends BaseHttpHandler {
    protected final MoviesStore moviesStore;

    public BaseHttpHandlerWithStore(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
    }

    protected String getQuery(String key, HttpExchange exchange) {
        String query = exchange.getRequestURI().getRawQuery();

        if (query == null || query.isEmpty()) {
            return null;
        }

        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts[0].equals(key))
                .map(parts -> parts.length > 1 ? parts[1] : "")
                .findFirst()
                .orElse(null);
    }
}
