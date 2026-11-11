package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    public void sendResponseWithBody(Object data, HttpExchange exchange, int rCode) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        Headers headers = exchange.getResponseHeaders();

        headers.set("Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(rCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    public void sendResponseWithoutBody(HttpExchange exchange, int rCode) throws IOException {
        exchange.sendResponseHeaders(rCode, -1);
    }


    public void handle(HttpExchange exchange) throws IOException {
    }
}

