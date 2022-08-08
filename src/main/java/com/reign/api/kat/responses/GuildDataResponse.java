package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reign.api.kat.models.ApiGuildData;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildDataResponse {
    public @JsonProperty("data") List<ApiGuildData> data;
    public @JsonProperty("msg") String message;
    public @JsonProperty("error") String err;
    public @JsonProperty("status") int status;

    public ApiGuildData get() {
        if (data.size() > 0)
            return data.get(0);
        return null;
    }
}
