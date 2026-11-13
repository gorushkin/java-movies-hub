package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.exception.AppException;
import ru.practicum.moviehub.exception.NotFoundException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


record Route(
        Predicate<HttpExchange> matches,
        BaseHttpHandler handler
) {
}


class MoviesRouter extends BaseHttpHandler {
    List<Route> routes = new ArrayList<>();
    HttpServer httpServer;

    public MoviesRouter(int port) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/", this);
    }

    private void addRoute(String method, String path, BaseHttpHandler handler) {
        Pattern pattern = Pattern.compile(path);

        var matches = new Predicate<HttpExchange>() {
            @Override
            public boolean test(HttpExchange httpExchange) {
                Matcher matcher = pattern.matcher(httpExchange.getRequestURI().getPath());
                return httpExchange.getRequestMethod().equals(method.toUpperCase(Locale.ROOT)) && matcher.matches();
            }
        };

        this.routes.add(new Route(matches, handler));
    }

    void get(String path, BaseHttpHandler handler) {
        addRoute("GET", path, handler);
    }

    void post(String path, BaseHttpHandler handler) {
        addRoute("POST", path, handler);
    }

    void delete(String path, BaseHttpHandler handler) {
        addRoute("DELETE", path, handler);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (routes.isEmpty()) {
                throw new NotFoundException();
            }

            var filteredRoutes = this.routes.stream().filter(r -> r.matches().test(exchange)).toList();

            if (filteredRoutes.isEmpty()) {
                throw new NotFoundException();
            }

            var route = this.routes.stream().filter(r -> r.matches().test(exchange)).toList().getFirst();

            if (route == null) {
                throw new NotFoundException();
            }

            route.handler().handle(exchange);
        } catch (AppException e) {
            if (e.getResponseBody() == null) {
                sendResponseWithoutBody(exchange, e.getResponseCode());
            }

            sendResponseWithBody(e.getResponseBody(), exchange, e.getResponseCode());
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        }

        exchange.sendResponseHeaders(404, -1);
    }

    public void start() {
        this.httpServer.start();
        System.out.println("Server started at http://localhost:" + httpServer.getAddress().getPort());
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stopped at http://localhost:" + httpServer.getAddress().getPort());
    }
}