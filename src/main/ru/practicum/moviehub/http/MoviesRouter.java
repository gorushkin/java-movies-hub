package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.exception.AppException;
import ru.practicum.moviehub.exception.NotAllowedMethod;
import ru.practicum.moviehub.exception.NotFoundRoute;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

class MoviesRouter extends BaseHttpHandler {
    List<Route> routes = new ArrayList<>();
    HttpServer httpServer;

    public MoviesRouter(int port) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/", this);
    }

    private void addRoute(String method, String path, BaseHttpHandler handler) {
        this.routes.add(new Route(
                method.toUpperCase(Locale.ROOT), Pattern.compile(path), handler));
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
            var routesByPath = this.routes.stream()
                    .filter(r -> r.path().matcher(exchange.getRequestURI().getPath()).matches())
                    .toList();

            if (routesByPath.isEmpty()) {
                throw new NotFoundRoute("Маршрут не найден");
            }

            var route = routesByPath.stream()
                    .filter(r -> r.method().equals(exchange.getRequestMethod()))
                    .findFirst()
                    .orElseThrow(() -> new NotAllowedMethod("Метод не поддерживается"));

            route.handler().handle(exchange);
        } catch (AppException e) {
            sendResponseWithBody(e.getResponseBody(), exchange, e.getResponseCode());
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        }
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
