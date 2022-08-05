package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class PermissionGroups {
    public @JsonProperty("admin") ArrayList<String> admin;
    public @JsonProperty("mod") ArrayList<String> mod;
}
