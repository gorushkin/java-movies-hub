package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MoviesServer {
    private static final int PORT = 8080;
    HttpServer server;

    public MoviesServer(MoviesStore moviesStore, int i) throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        System.out.println("server = " + server.getAddress());

        server.createContext("/movies", new GetMoviesHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        System.out.println("MoviesServer.stop");
        server.stop(0);
    }
}