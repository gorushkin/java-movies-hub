package ru.practicum.moviehub.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesApiTest {

    @BeforeAll
    static void beforeAll() {

    }

    @AfterAll
    static void afterAll() {

    }

    @BeforeEach
    void beforeEach() throws IOException {
    }

    private MoviesServer getServer() throws IOException {
        return new MoviesServer(new MoviesStore(), 8080);
    }

    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws Exception {
        MoviesServer server = getServer();
        server.start();

        HttpClient client = HttpClient.newHttpClient();

        String baseUrl = "http://localhost:8080";
        URI url = URI.create(baseUrl + "/movies");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

//        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");

        String contentTypeHeaderValue =
                response.headers().firstValue("Content-Type").orElse("");

        assertEquals("", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = response.body().trim();
        assertTrue(body.startsWith("[") && body.endsWith("]"),
                "Ожидается JSON-массив");
    }
}