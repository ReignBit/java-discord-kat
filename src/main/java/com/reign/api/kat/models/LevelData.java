package com.reign.api.kat.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LevelData {
    public @JsonProperty("enabled") boolean enabled;
    public @JsonProperty("xp_multiplier") float xpMultiplier;
}
