package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildResponse {
    public @JsonProperty("data") List<ApiGuild> data;
    public @JsonProperty("msg") String message;
    public @JsonProperty("error") String err;
    public @JsonProperty("status") int status;

    public ApiGuild get() {
        if (data.size() > 0)
            return data.get(0);
        return null;
    }
}

