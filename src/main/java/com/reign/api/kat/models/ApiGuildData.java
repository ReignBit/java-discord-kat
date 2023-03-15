package com.reign.api.kat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.reign.api.kat.Endpoints;
import com.reign.api.kat.responses.GuildDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGuildData extends ApiModel {
    private static final Logger log = LoggerFactory.getLogger(ApiGuildData.class);

    public @JsonProperty("snowflake") String snowflake;
    public @JsonProperty("level") LevelData level;

    public ApiGuildData(String snowflake)
    {
        this.snowflake = snowflake;
        this.level = new LevelData();
    }
    /**
     * Contains data and settings about the Level System.
     */
    public static class LevelData {
        public @JsonProperty("enabled") boolean enabled;
        public @JsonProperty("xp_multiplier") float xpMultiplier;
        public LevelData()
        {
            this.enabled = true;
            this.xpMultiplier = 1.0f;
        }
    }

    public static ApiGuildData get(String snowflake)
    {
        GuildDataResponse g = fetch(Endpoints.buildEndpoint(Endpoints.GuildData, snowflake), GuildDataResponse.class);
        log.info(String.valueOf(g.data));
        if (!g.data.isEmpty())
        {
            return g.get();
        }

        // Guild doesn't exist in api, lets create it.
        ApiGuildData newGuild = new ApiGuildData(snowflake);
        newGuild.save();
        return newGuild;
    }

    @Override
    public boolean save() {
        return commit(Endpoints.buildEndpoint(Endpoints.GuildData, snowflake), this, GuildDataResponse.class);
    }
}
