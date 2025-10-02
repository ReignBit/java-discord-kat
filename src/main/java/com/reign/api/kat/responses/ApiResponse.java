package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reign.api.kat.models.ApiModel;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T extends ApiModel> {
    public @JsonProperty("data") List<T> data;
    public @JsonProperty("msg") String message;
    public @JsonProperty("error") String err;
    public @JsonProperty("status") int status;

    public T get() {
        if (!data.isEmpty())
            return data.get(0);
        return null;
    }
}
