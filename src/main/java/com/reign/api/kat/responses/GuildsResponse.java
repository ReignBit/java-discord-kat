package com.reign.api.kat.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GuildsResponse(ArrayList<ApiGuild> guilds, String message, String err, int status) {
    public GuildsResponse(
            @JsonProperty("data") ArrayList<ApiGuild> guilds,
            @JsonProperty("msg") String message,
            @JsonProperty("error") String err,
            @JsonProperty("status") int status
    )
    {
        this.guilds = guilds;
        this.message = message;
        this.err = err;
        this.status = status;
    }
}
