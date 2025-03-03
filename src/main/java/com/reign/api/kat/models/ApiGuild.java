package com.reign.api.kat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reign.api.kat.ApiCache;
import com.reign.api.kat.Endpoints;
import com.reign.api.kat.responses.GuildResponse;
import com.reign.api.kat.responses.PermissionGroups;
import com.reign.kat.lib.Config;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGuild extends ApiModel
{


    public @JsonProperty("snowflake") String snowflake;
    public @JsonProperty("discovered_at") Long discoveredAt;
    public @JsonProperty("members") ArrayList<String> members;
    public @JsonProperty("prefix") String prefix;
    //public @JsonProperty("dashboard_enabled") boolean dashboardEnabled;
    public @JsonProperty("command") Document commandData;
    public @JsonProperty("permission_groups") PermissionGroups permissionGroups;

    protected static ApiCache<ApiGuild> cache = new ApiCache<>(ApiGuild.class);

    public ApiGuild()
    {
        super();
        this.discoveredAt = Instant.now().getEpochSecond();
        this.prefix = Config.PREFIX;
        this.commandData = new Document();
    }
    public ApiGuild(String snowflake)
    {
        super();
        this.snowflake = snowflake;
        this.discoveredAt = Instant.now().getEpochSecond();
        this.prefix = Config.PREFIX;
        this.commandData = new Document();
    }

    public PermissionGroups getPermissionGroups() {
        return permissionGroups;
    }

    public String getSnowflake() {
        return snowflake;
    }

    public Long getDiscoveredAt() {
        return discoveredAt;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getPrefix() {
        return prefix;
    }

    public Object getCommandData(String key)
    {
        return commandData.get(key);
    }

    public static void invalidateCache()
    {
        cache.clear();
    }

    @Override
    public boolean save() {
        boolean result = commit(Endpoints.buildEndpoint(Endpoints.Guild, snowflake), this, GuildResponse.class);
        if (result)
        {
            cache.upsert(snowflake, this);
        }
        return result;
    }

    public static ApiGuild get(String snowflake) {
        ApiGuild hit = cache.get(snowflake);

        if (hit == null)
        {
            GuildResponse g = fetch(Endpoints.buildEndpoint(Endpoints.Guild, snowflake), GuildResponse.class);
            if (g.status == 200 && !g.data.isEmpty())
            {
                log.info("Fetched guild id {}", snowflake);
                ApiGuild d = g.get();
                cache.upsert(snowflake, d);
                return d;
            }

            log.info("Creating new guild");
            // Guild doesn't exist in api, lets create it.
            ApiGuild newGuild = new ApiGuild(snowflake);
            newGuild.save();
            log.info("New guild created {}", newGuild);
            cache.upsert(snowflake, newGuild);
            return newGuild;
        }
        return hit;
    }
}
