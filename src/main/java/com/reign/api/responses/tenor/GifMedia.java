package com.reign.api.responses.tenor;

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
