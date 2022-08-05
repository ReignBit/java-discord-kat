package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGuild {


    public @JsonProperty("snowflake") String snowflake;
    public @JsonProperty("discovered_at") Long discoveredAt;
    public @JsonProperty("members") ArrayList<String> members;
    public @JsonProperty("prefix") String prefix;
    //public @JsonProperty("dashboard_enabled") boolean dashboardEnabled;
    public @JsonProperty("owner_id") String ownerId;
    public @JsonProperty("permission_groups") PermissionGroups permissionGroups;

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

    public String getOwnerId() {
        return ownerId;
    }

    public String toString()
    {
        ObjectMapper map = new ObjectMapper();
        try {
            return map.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
