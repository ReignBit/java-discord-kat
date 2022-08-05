package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class HelloResponse {
     public @JsonProperty("data") int[] data;
     public @JsonProperty("msg") String message;
     public @JsonProperty("error") String err;
     public @JsonProperty("status") int status;
}