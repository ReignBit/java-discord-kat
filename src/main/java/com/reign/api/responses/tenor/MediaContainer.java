package com.reign.api.responses.tenor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MediaContainer(GifMedia mp4, GifMedia tinyGif, GifMedia mediumGif, GifMedia gif) {
    public MediaContainer(
            @JsonProperty("mp4") GifMedia mp4,
            @JsonProperty("tinyGif") GifMedia tinyGif,
            @JsonProperty("mediumGif") GifMedia mediumGif,
            @JsonProperty("gif") GifMedia gif
    ) {
        this.mp4 = mp4;
        this.tinyGif = tinyGif;
        this.mediumGif = mediumGif;
        this.gif = gif;
    }

}
