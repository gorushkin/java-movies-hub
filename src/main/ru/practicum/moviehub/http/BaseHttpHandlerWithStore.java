package ru.practicum.moviehub.http;

import ru.practicum.moviehub.store.MoviesStore;

public abstract class BaseHttpHandlerWithStore extends BaseHttpHandler {

    protected final MoviesStore moviesStore;

    public BaseHttpHandlerWithStore(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
    }
}
