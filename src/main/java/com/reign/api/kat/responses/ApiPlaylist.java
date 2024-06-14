package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiPlaylist
{
    public @JsonProperty("name") String name;
    public @JsonProperty("tracks") ArrayList<String> tracks;
    public @JsonProperty("date_created") long dateCreated;
    public @JsonProperty("author_snowflake") String author;

    public String toString()
    {
        return String.format("%s { author: %s, created: %d, tracks: %s", name, author, dateCreated, tracks.toString());
    }
}
