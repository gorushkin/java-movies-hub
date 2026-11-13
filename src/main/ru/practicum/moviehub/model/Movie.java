package ru.practicum.moviehub.model;
import java.util.Objects;

public class Movie {
    private final String title;
    private final int year;
    private final int id;

    public Movie(int id, int year, String title) {
        String trimmedTitle = title.trim();

        this.id = id;
        this.year = year;
        this.title = trimmedTitle;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return year == movie.year && id == movie.id && Objects.equals(title, movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year, id);
    }
}