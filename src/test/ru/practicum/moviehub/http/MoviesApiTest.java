package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.practicum.moviehub.dto.ErrorResponse;
import ru.practicum.moviehub.dto.MovieDTO;
import ru.practicum.moviehub.exception.MovieValidationException;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesApiTest {
    MoviesServer server;
    MoviesStore movieStore;
    UrlBuilder urlBuilder;

    @BeforeEach
    void beforeEach() throws IOException {
        int port = 8085;
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

        assertJsonContentType(response);

        int expectedStatusCode = 200;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

        String contentTypeHeaderValue =
                response.headers().firstValue("Content-Type").orElse("");

        assertEquals("application/json; charset=UTF-8", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = response.body().trim();

        assertEquals("[]", body, "Ожидается пустой JSON-массив");
    }

    @Test
    void getMovies_whenMoviesExist_returnsStoredMovies() throws Exception {

        movieStore.addMovie(2015, "move1");
        movieStore.addMovie(2020, "move2");


        var movies = movieStore.getMovies();

        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.buildUri();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

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
    void addMovie_whenDataIsValid_returnsCreatedMovie() throws Exception {
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

        assertJsonContentType(response);

        int expectedStatusCode = 201;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Movie parsedMovie = gson.fromJson(body, Movie.class);
        assertTrue(parsedMovie.getId() > 0);
        assertEquals(parsedMovie, movieStore.getMovieById(parsedMovie.getId()));
        Assertions.assertEquals(data1.year().intValue(), parsedMovie.getYear(), "Год добавленного фильма совпадает");
        Assertions.assertEquals(data1.title(), parsedMovie.getTitle(), "Название добавленного фильма совпадает");
    }

    @Test
    void addMovie_whenTitleIsEmpty_returnsValidationError() throws Exception {
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

        assertJsonContentType(response);

        int expectedStatusCode = 422;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);


        Assertions.assertEquals(MovieValidationException.errorName, parsedResponse.getError());
        Assertions.assertEquals(1, parsedResponse.getDetails().size());
    }

    @Test
    void addMovie_whenBodyIsNull_returnsBadRequest() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.buildUri();

        Gson gson = new Gson();
        String body1 = gson.toJson(null);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 400;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies должен вернуть" + expectedStatusCode);
    }

    @Test
    void addMovie_whenContentTypeIsMissing_returnsUnsupportedMediaType() throws Exception {
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

        assertJsonContentType(response);

        int expectedStatusCode = 415;

        assertEquals(expectedStatusCode, response.statusCode(), "POST /movies должен вернуть" + expectedStatusCode);
    }

    @Test
    void getMovieById_whenMovieExists_returnsMovie() throws Exception {
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

        assertJsonContentType(response);

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
    void getMovieById_whenMovieDoesNotExist_returnsNotFound() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.addPathSegment("5").buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 404;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies:id должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals("Фильм не найден", parsedResponse.getError());
    }

    @Test
    void getMovieById_whenIdIsNotNumeric_returnsBadRequest() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.addPathSegment("id").buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 400;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies:id должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals("Некорректный ID", parsedResponse.getError());
    }

    @Test
    void getMovies_whenYearIsSpecified_returnsMatchingMovies() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        int targetYear = LocalDate.now().getYear();

        int targetYearMoviesCount = 3;
        int otherYearsMoviesCount = 5;

        List<Movie> targetMovies = new ArrayList<>();

        for (int i = 0; i < targetYearMoviesCount; i++) {
            targetMovies.add(movieStore.addMovie(targetYear, "targetYearMovieTitle" + i));
        }

        for (int i = 0; i < otherYearsMoviesCount; i++) {
            movieStore.addMovie(targetYear - 1 - i, "nonTargetYearMovieTitle" + i);
        }

        URI url = urlBuilder
                .addQueryParameter("year", targetYear)
                .buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 200;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies?year должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();

        List<Movie> parsedMovies = gson.fromJson(body, new ListOfMoviesTypeToken().getType());

        Assertions.assertEquals(targetMovies.size(), parsedMovies.size());
        Assertions.assertEquals(targetMovies, parsedMovies);
    }

    @Test
    void getMovies_whenYearIsNotNumeric_returnsBadRequest() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder
                .addQueryParameter("year", "wrongYearValue")
                .buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 400;

        assertEquals(expectedStatusCode, response.statusCode(), "GET /movies?year должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals("Некорректный параметр запроса — 'year'", parsedResponse.getError());
    }

    @Test
    void deleteMovie_whenMovieExists_returnsNoContent() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        MovieDTO movieData = new MovieDTO(LocalDate.now().getYear(), "movie1");
        var addedMovie = movieStore.addMovie(movieData.year(), movieData.title());
        String id = String.valueOf(addedMovie.getId());

        URI url = urlBuilder.addPathSegment(id).buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 204;

        assertEquals(expectedStatusCode, response.statusCode(), "DELETE /movies должен вернуть" + expectedStatusCode);
        assertEquals("", response.body());
        Assertions.assertNull(movieStore.getMovieById(addedMovie.getId()));
    }

    @Test
    void deleteMovie_whenMovieDoesNotExist_returnsNotFound() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.addPathSegment("15").buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 404;

        assertEquals(expectedStatusCode, response.statusCode(), "DELETE /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals("Фильм не найден", parsedResponse.getError());
    }

    @Test
    void deleteMovie_whenIdIsNotNumeric_returnsBadRequest() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI url = urlBuilder.addPathSegment("wrongFimId").buildUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler =
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        HttpResponse<String> response = client.send(request, responseBodyHandler);

        assertJsonContentType(response);

        int expectedStatusCode = 400;

        assertEquals(expectedStatusCode, response.statusCode(), "DELETE /movies должен вернуть" + expectedStatusCode);

        String body = response.body().trim();

        Gson gson = new Gson();
        ErrorResponse parsedResponse = gson.fromJson(body, ErrorResponse.class);

        Assertions.assertEquals("Некорректный ID", parsedResponse.getError());
    }

    @Test
    void addMovie_whenJsonIsMalformed_returnsBadRequest() throws Exception {
        var response = sendPost("application/json", "{broken");

        assertErrorResponse(response, 400);
        assertEquals(0, movieStore.size());
    }

    @Test
    void addMovie_whenContentTypeIsTextPlain_returnsUnsupportedMediaType() throws Exception {
        var response = sendPost("text/plain", "{\"title\":\"Movie\",\"year\":2000}");

        assertErrorResponse(response, 415);
        assertEquals(0, movieStore.size());
    }

    @Test
    void addMovie_whenContentTypeHasInvalidSuffix_returnsUnsupportedMediaType() throws Exception {
        var response = sendPost("application/json-invalid", "{\"title\":\"Movie\",\"year\":2000}");

        assertErrorResponse(response, 415);
        assertEquals(0, movieStore.size());
    }

    @Test
    void getMovies_whenNoMoviesMatchYear_returnsEmptyArray() throws Exception {
        movieStore.addMovie(2000, "Other year");
        var request = HttpRequest.newBuilder()
                .uri(urlBuilder.addQueryParameter("year", 2001).buildUri())
                .GET()
                .build();

        var response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertJsonContentType(response);
        assertEquals("[]", response.body().trim());
    }

    @Test
    void updateMovies_whenMethodIsUnsupported_returnsMethodNotAllowed() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(urlBuilder.buildUri())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        var response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertErrorResponse(response, 405);
    }

    @Test
    void addMovie_whenYearIsNull_returnsValidationError() throws Exception {
        assertMissingYearIsRejected("{\"title\":\"Movie\",\"year\":null}");
    }

    @Test
    void addMovie_whenYearIsMissing_returnsValidationError() throws Exception {
        assertMissingYearIsRejected("{\"title\":\"Movie\"}");
    }

    private void assertMissingYearIsRejected(String body) throws Exception {
        var response = sendPost("application/json", body);

        assertErrorResponse(response, 422);
        var error = new Gson().fromJson(response.body(), ErrorResponse.class);
        assertEquals(MovieValidationException.errorName, error.getError());
        assertEquals(List.of("год должен быть указан"), error.getDetails());
        assertEquals(0, movieStore.size());
    }

    private HttpResponse<String> sendPost(String contentType, String body) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(urlBuilder.buildUri())
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private void assertJsonContentType(HttpResponse<String> response) {
        assertEquals("application/json; charset=UTF-8",
                response.headers().firstValue("Content-Type").orElse(""));
    }

    private void assertErrorResponse(HttpResponse<String> response, int expectedStatus) {
        assertEquals(expectedStatus, response.statusCode());
        assertJsonContentType(response);
        var error = new Gson().fromJson(response.body(), ErrorResponse.class);
        Assertions.assertNotNull(error);
        Assertions.assertNotNull(error.getError());
        assertFalse(error.getError().isBlank());
    }
}
