package com.reign.api.tenor.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Random;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TenorGifs {

    public long requestedAt;
    public final Gif[] results;
    public final String next;

    public TenorGifs(
            @JsonProperty("results") Gif[] results,
            @JsonProperty("next") String next
    ) {
        this.results = results;
        this.next = next;
        requestedAt = Instant.now().toEpochMilli();
    }

    public TenorGifs get() {
        return this;
    }

    public Gif[] results() {
        return this.results;
    }

    public GifMedia getRandomGif()
    {
        int random = new Random().nextInt(results.length);
        return results[random].getGif();
    }
}

