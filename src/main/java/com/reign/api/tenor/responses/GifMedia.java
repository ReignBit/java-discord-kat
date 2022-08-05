package com.reign.api.tenor.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GifMedia(int size, String url) {
    public GifMedia(
            @JsonProperty("size") int size,
            @JsonProperty("url") String url
    ) {
        this.size = size;
        this.url = url;
    }
}
