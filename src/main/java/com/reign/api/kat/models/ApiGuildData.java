package com.reign.api.kat.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGuildData {
    public @JsonProperty("snowflake") String snowflake;
    public @JsonProperty("level") LevelData level;
}
