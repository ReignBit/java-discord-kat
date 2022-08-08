package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildPrefixResponse {
    public @JsonProperty("data") String data;
    public @JsonProperty("msg") String message;
    public @JsonProperty("error") String err;
    public @JsonProperty("status") int status;

    public String get() {
        return data;
    }
}

