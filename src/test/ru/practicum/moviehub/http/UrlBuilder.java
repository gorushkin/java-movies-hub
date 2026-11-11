package ru.practicum.moviehub.http;

import java.net.URI;

class UrlBuilder {
    String url;

    boolean hasQueryParams = false;

    public UrlBuilder(String baseUrl, int port) {
        this.url = baseUrl + ":" + port;
    }

    UrlBuilder addPathSegment(String segment) {
        this.url = this.url + "/" + segment;
        return this;
    }

    UrlBuilder addQueryParameter(String key, String value) {
        var sep = hasQueryParams ? "&" : "?";

        this.url = this.url + sep + key + "=" + value;

        return this;
    }

    URI buildUri() {
        return URI.create(this.url);
    }
}
