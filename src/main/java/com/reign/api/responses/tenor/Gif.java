package com.reign.api.responses.tenor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Gif(String id, MediaContainer[] media, String itemUrl) {
    public Gif(
            @JsonProperty("id") String id,
            @JsonProperty("media") MediaContainer[] media,
            @JsonProperty("item_url") String itemUrl
    ) {
        this.id = id;
        this.media = media;
        this.itemUrl = itemUrl;
    }


    public GifMedia getMp4() {
        return media[0].mp4();
    }

    public GifMedia getTinyGif() {
        return media[0].tinyGif();
    }

    public GifMedia getMediumGif() {
        return media[0].mediumGif();
    }

    public GifMedia getGif() {
        return media[0].gif();
    }


}
