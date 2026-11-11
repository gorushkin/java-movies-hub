package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.practicum.moviehub.dto.ErrorResponse;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.MovieValidationException;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesApiTest {
    private final int port = 8080;
    MoviesServer server;
    MoviesStore movieStore;

    @BeforeAll
    static void beforeAll() {

    }

    @AfterAll
    static void afterAll() {
    }

    private URI getUrl(String path) {
        String baseUrl = "http://localhost:" + port;

        if (path.isEmpty()) {
            return URI.create(baseUrl + "/movies");
        }

        return URI.create(baseUrl + "/movies" + path);
    }

    private URI getUrl() {
        return getUrl("");
    }

    @BeforeEach
    void beforeEach() throws IOException {
        this.movieStore = new MoviesStore();
        this.server = new MoviesServer(this.movieStore, port);
        server.start();
    }

    @AfterEach
    void afterEach() throws IOException {
        this.server.stop();
    }


    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = getUrl();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");

        String contentTypeHeaderValue =
                response.headers().firstValue("Content-Type").orElse("");

        assertEquals("application/json; charset=UTF-8", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = response.body().trim();

        assertTrue(body.startsWith("[") && body.endsWith("]"),
                "Ожидается JSON-массив");
    }

    @Test
    void getMovies_whenHasTwoMovies_returnsArrayWithTwoMovies() throws Exception {

        movieStore.addMovie(2015, "move1");
        movieStore.addMovie(2020, "move2");

        System.out.println("movieStore = " + movieStore);

        var movies = movieStore.getAllMovies();

        HttpClient client = HttpClient.newHttpClient();

        URI url = getUrl();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertEquals(200, response.statusCode(), "GET /movies должен вернуть 200");

        String contentTypeHeaderValue =
                response.headers().firstValue("Content-Type").orElse("");

        assertEquals("application/json; charset=UTF-8", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = response.body().trim();

        Gson gson = new Gson();

        List<Movie> parsedMovies = gson.fromJson(body, new ListOfMoviesTypeToken().getType());

        Assertions.assertEquals(movies.size(), parsedMovies.size());
        Assertions.assertEquals(movies, parsedMovies);
    }

    @Test
    void addMovieSuccessfully() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = getUrl();

        MovieDTO data1 = new MovieDTO(LocalDate.now().getYear(), "movie1");

        Gson gson = new Gson();

        String body1 = gson.toJson(data1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertEquals(201, response.statusCode(), "POST /movies должен вернуть 201");

        String body = response.body().trim();

        Movie parsedMovie = gson.fromJson(body, Movie.class);
        Assertions.assertEquals(data1.year(), parsedMovie.getYear(), "Год добавленного фильма совпадает");
        Assertions.assertEquals(data1.title(), parsedMovie.getTitle(), "Название добавленного фильма совпадает");
    }


    @Test
    void returnValidationError_whenTitleIsNotValid() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = getUrl();

        MovieDTO data1 = new MovieDTO(LocalDate.now().getYear(), "");

        Gson gson = new Gson();

        String body1 = gson.toJson(data1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertEquals(422, response.statusCode(), "POST /movies должен вернуть 201");

        String body = response.body().trim();
        System.out.println("body = " + body);

        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals(MovieValidationException.errorName, parsedResponse.error());
        Assertions.assertEquals(1, parsedResponse.details().size());
    }

    @Test
    void returnUnsupportedMediaTypeError_whenContentTypeIsWrong() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = getUrl();

        MovieDTO data1 = new MovieDTO(LocalDate.now().getYear(), "");

        Gson gson = new Gson();

        String body1 = gson.toJson(data1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertEquals(415, response.statusCode(), "POST /movies должен вернуть 415");
    }
}