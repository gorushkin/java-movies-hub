package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.practicum.moviehub.dto.ErrorResponse;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.BadRequestException;
import ru.practicum.moviehub.exception.MovieValidationException;
import ru.practicum.moviehub.exception.NotFoundMovieException;
import ru.practicum.moviehub.model.Movie;
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
    MoviesServer server;
    MoviesStore movieStore;
    UrlBuilder urlBuilder;

    @BeforeAll
    static void beforeAll() {

    }

    @AfterAll
    static void afterAll() {
    }


    @BeforeEach
    void beforeEach() throws IOException {
        int port = 8080;
        this.urlBuilder = new UrlBuilder("http://localhost", port).addPathSegment("movies");
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

        URI url = urlBuilder.buildUri();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        int expectedStatusCode = 200;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

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

        URI url = urlBuilder.buildUri();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        int expectedStatusCode = 200;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

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

        URI url = urlBuilder.buildUri();

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

        int expectedStatusCode = 201;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Movie parsedMovie = gson.fromJson(body, Movie.class);
        Assertions.assertEquals(data1.year(), parsedMovie.getYear(), "Год добавленного фильма совпадает");
        Assertions.assertEquals(data1.title(), parsedMovie.getTitle(), "Название добавленного фильма совпадает");
    }

    @Test
    void returnValidationError_whenTitleIsNotValid() throws Exception {
        HttpClient client = HttpClient.newHttpClient();


        URI url = urlBuilder.buildUri();

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

        int expectedStatusCode = 422;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        parsedResponse.getDetails().forEach(System.out::println);

        Assertions.assertEquals(MovieValidationException.errorName, parsedResponse.getError());
        Assertions.assertEquals(1, parsedResponse.getDetails().size());
    }

    @Test
    void returnUnsupportedMediaTypeError_whenContentTypeIsWrong() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.buildUri();

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

        int expectedStatusCode = 415;

        assertEquals(expectedStatusCode, response.statusCode(), "POST /movies должен вернуть" + expectedStatusCode);
    }

    @Test
    void returnMovieByIdSuccessfully() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        MovieDTO movieData = new MovieDTO(LocalDate.now().getYear(), "movie1");
        var addedMovie = movieStore.addMovie(movieData.year(), movieData.title());
        String id = String.valueOf(addedMovie.getId());

        URI url = urlBuilder.addPathSegment(id).buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        int expectedStatusCode = 200;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        Movie parsedResponse = gson.fromJson(body, Movie.class);

        assertEquals(addedMovie.getId(), parsedResponse.getId());
        assertEquals(addedMovie.getTitle(), parsedResponse.getTitle());
        assertEquals(addedMovie.getYear(), parsedResponse.getYear());
    }

    @Test
    void returnNotFoundErrorIfMovieNotFround() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.addPathSegment("5").buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        int expectedStatusCode = 404;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies:id должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals(NotFoundMovieException.errorName, parsedResponse.getError());
    }

    @Test
    void returnBadRequestErrorIfIdIsNotProper() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.addPathSegment("asdfasdfadf").buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        int expectedStatusCode = 400;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies:id должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals(BadRequestException.errorName, parsedResponse.getError());
    }
}