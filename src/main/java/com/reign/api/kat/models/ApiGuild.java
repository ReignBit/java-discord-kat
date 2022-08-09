package com.reign.api.kat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reign.api.kat.Endpoints;
import com.reign.api.kat.responses.GuildResponse;
import com.reign.api.kat.responses.PermissionGroups;
import com.reign.kat.Bot;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGuild extends ApiModel {


    public @JsonProperty("snowflake") String snowflake;
    public @JsonProperty("discovered_at") Long discoveredAt;
    public @JsonProperty("members") ArrayList<String> members;
    public @JsonProperty("prefix") String prefix;
    //public @JsonProperty("dashboard_enabled") boolean dashboardEnabled;
    @Deprecated
    public @JsonProperty("owner_id") String ownerId;
    public @JsonProperty("permission_groups") PermissionGroups permissionGroups;

    public ApiGuild()
    {
        super();
    }
    public ApiGuild(String snowflake)
    {
        super();
        this.snowflake = snowflake;
        this.discoveredAt = Instant.now().getEpochSecond();
        this.prefix = Bot.properties.getPrefix();
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

    @Deprecated
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean save() {
        return commit(Endpoints.buildEndpoint(Endpoints.Guild, snowflake), this, GuildResponse.class);
    }

    public static ApiGuild get(String snowflake) {
        GuildResponse g = fetch(Endpoints.buildEndpoint(Endpoints.Guild, snowflake), GuildResponse.class);
        if (g.status == 200 && !g.data.isEmpty())
        {
            log.info("Fetched guild id {}", snowflake);
            return g.get();
        }

        log.info("Creating new guild");
        // Guild doesn't exist in api, lets create it.
        ApiGuild newGuild = new ApiGuild(snowflake);
        newGuild.save();
        log.info("New guild created {}", newGuild);
        return newGuild;
    }
}
