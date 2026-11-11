package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MoviesServer {
    HttpServer httpServer;

    public MoviesServer(MoviesStore moviesStore, int port) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);

        var router = new MoviesRouter();

        router.get("/movies", new GetMoviesHandler(moviesStore));
        router.post("/movies", new AddMovieHandler(moviesStore));
        router.get("/movies/[^/]+", new GetMovieByIdHandler(moviesStore));
        router.delete("/movies/[^/]+", new DeleteMovieById(moviesStore));

        httpServer.createContext("/movies", router);
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
