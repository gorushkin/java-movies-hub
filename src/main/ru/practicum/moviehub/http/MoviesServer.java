package ru.practicum.moviehub.http;

import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class MoviesServer {
    MoviesRouter httpServer;

    public MoviesServer(MoviesStore moviesStore, int port) throws IOException {
        httpServer = new MoviesRouter(port);

        httpServer.get("/movies", new GetMoviesHandler(moviesStore));
        httpServer.post("/movies", new AddMovieHandler(moviesStore));
        httpServer.get("/movies/[^/]+", new GetMovieByIdHandler(moviesStore));
        httpServer.delete("/movies/[^/]+", new DeleteMovieById(moviesStore));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop();
    }
}
