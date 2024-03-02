package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionGroups {
    public @JsonProperty("admin") ArrayList<String> admin;
    public @JsonProperty("mod") ArrayList<String> mod;
}
