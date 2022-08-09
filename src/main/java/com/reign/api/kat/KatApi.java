package com.reign.api.kat;

import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.models.ApiGuildData;
import com.reign.api.kat.models.ApiMemberData;
import com.reign.api.kat.models.ApiModel;
import com.reign.api.kat.responses.*;
import com.reign.api.lib.JsonBodyHandler;
import com.reign.kat.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class KatApi {
    private static final Logger log = LoggerFactory.getLogger(KatApi.class);
    private static final HttpClient client = HttpClient.newHttpClient();
    public static String host;
    public static String authStr;

    public KatApi(String host, String authentication) {
        KatApi.host = host;
        KatApi.authStr = authentication;

    }

    public static HttpClient getClient()
    {
        return client;
    }

//
//    /**
//     * Retrieve a Guild from the Api with snowflake = id.
//     * @param id Snowflake of the guild
//     * @return ApiGuild - Guild data retrieved from api.
//     */
//    public ApiGuild getGuild(String id)
//    {
//        GuildResponse resp = get(String.format("%s/%s", Endpoints.Guilds, id), GuildResponse.class);
//        if (resp != null && resp.data != null)
//        {
//            log.debug(resp.get().toString());
//            return resp.get();
//        }
//        return null;
//    }
//
//    /**
//     * Retrieve a GuildData entry from the Api, creating it in the Api if ensureExists = true
//     * @param id Snowflake of the guild
//     * @param ensureExists If true, POST request to guilds and create an entry
//     *                     for the guild if an entry doesnt already exist
//     * @return ApiGuild - GuildData data retrieved from api.
//     */
//    public ApiGuildData getGuildData(String id, boolean ensureExists)
//    {
//        GuildDataResponse resp = get(String.format(Endpoints.GuildData, id), GuildDataResponse.class);
//        if (resp != null) {
//            if (resp.get() == null && ensureExists) {
//                // Guild doesn't exist in the Api.
//                return createGuildData(id);
//            }
//
//            log.debug(resp.get().toString());
//            return resp.get();
//        }
//        return null;
//    }
//
//    /**
//     * Retrieve a MemberData entry from the Api, creating it in the Api if ensureExists = true
//     * @param guildId Snowflake of the guild.
//     * @param userId Snowflake of the user.
//     * @param ensureExists If true, POST request to guilds and create an entry
//     *                     for the member if an entry doesnt already exist
//     * @return ApiMemberData - MemberData data retrieved from api.
//     */
////    public ApiMemberData getMemberData(String guildId, String userId, boolean ensureExists)
////    {
////        MemberDataResponse resp = get(String.format(Endpoints.MemberData, guildId, userId), MemberDataResponse.class);
////        if (resp != null) {
////            if (resp.get() == null && ensureExists) {
////                // Entry doesn't exist in the Api.
////                return createMemberData(guildId, userId);
////            }
////
////            log.debug(resp.get().toString());
////            return resp.get();
////        }
////        return null;
////    }
//
//    /**
//     * Create a new memberData entry in the api.
//     * @param guildId Snowflake of the guild.
//     * @param userId Snowflake of the user.
//     * @return A new ApiMemberData with the required data loaded.
//     */
////    public ApiMemberData createMemberData(String guildId, String userId)
////    {
////        ApiMemberData newMemberData = new ApiMemberData();
////        newMemberData.guildSnowflake = userId;
////        newMemberData.snowflake = userId;
////
////        MemberDataResponse resp = post(String.format(Endpoints.MemberData, guildId, userId), MemberDataResponse.class, newMemberData);
////        if (resp != null)
////        {
////            log.debug("Created new memberData Entry {}", resp.get().toString());
////            return resp.get();
////        }
////        return null;
////    }
//
//    /**
//     * Update a member's data entry in the api.
//     * @param memberData ApiMemberData data to update.
//     * @return The updated ApiMemberData object.
//     */
////    public ApiMemberData updateMemberData(ApiMemberData memberData)
////    {
////        MemberDataResponse resp = post(String.format(Endpoints.MemberData, memberData.guildSnowflake, memberData.snowflake), MemberDataResponse.class, memberData.toString());
////        if (resp != null)
////        {
////            return resp.get();
////        }
////        return null;
////    }
//
//    /**
//     * Create a new guildData entry in the api.
//     * @param id Snowflake of the guild.
//     * @return A new ApiGuildData with the required data loaded.
//     */
//    public ApiGuildData createGuildData(String id)
//    {
//        ApiGuildData newGuildData = new ApiGuildData();
//        newGuildData.snowflake = id;
//
//        GuildDataResponse resp = post(String.format("%s/%s/data", Endpoints.Guilds, id), GuildDataResponse.class, newGuildData);
//        if (resp != null)
//        {
//            log.debug("Created new Guild Entry {}", resp.get().toString());
//            return resp.get();
//        }
//        return null;
//    }
//
//    /**
//     * Update a guild's data entry in the api.
//     * @param guildData ApiGuildData data to update.
//     * @return The updated ApiGuild object.
//     */
//    public ApiGuildData updateGuildData(ApiGuildData guildData)
//    {
//        GuildDataResponse resp = post(String.format(Endpoints.GuildData, guildData.snowflake), GuildDataResponse.class, guildData.toString());
//        if (resp != null)
//        {
//            return resp.get();
//        }
//        return null;
//    }
//
//    /**
//     * Retrieve a Guild from the Api, creating it in the Api if ensureExists = true
//     * @param id Snowflake of the guild
//     * @param ensureExists If true, POST request to guilds and create an entry
//     *                     for the guild if an entry doesnt already exist
//     * @return ApiGuild - Guild data retrieved from api.
//     */
//    public ApiGuild getGuild(String id, boolean ensureExists)
//    {
//        GuildResponse resp = get(String.format(Endpoints.Guild, id), GuildResponse.class);
//        if (resp != null) {
//            if (resp.get() == null && ensureExists) {
//                // Guild doesn't exist in the Api.
//                return createGuild(id);
//            }
//
//            log.debug(resp.get().toString());
//            return resp.get();
//        }
//        return null;
//    }
//
//    public String getGuildPrefix(String id, boolean ensureExists)
//    {
//        GuildPrefixResponse resp = get(String.format(Endpoints.GuildPrefix, id), GuildPrefixResponse.class);
//        if (resp != null)
//        {
//            if (resp.get() == null && ensureExists)
//            {
//                return createGuild(id).getPrefix();
//            }
//
//            return resp.data;
//        }
//        log.error("Something horribly wrong has happened when trying to request the prefix for guild ${id}");
//        return null;
//    }
//
//    /**
//     * Gets all guilds from the Api.
//     * @return ArrayList type ApiGuild
//     */
//    public ArrayList<ApiGuild> getGuilds() {
//        GuildsResponse resp = get(Endpoints.Guilds, GuildsResponse.class);
//        if (resp != null)
//        {
//            return resp.guilds();
//        }
//        return new ArrayList<>();
//    }
//
//    /**
//     * Create a new guild entry in the api.
//     * @param id Snowflake of the guild.
//     * @return A new ApiGuild with the required data loaded.
//     */
////    public ApiGuild createGuild(String id)
////    {
////        ApiGuild newGuild = new ApiGuild();
////        newGuild.snowflake = id;
////        newGuild.discoveredAt = Instant.now().getEpochSecond();
////        newGuild.ownerId = Objects.requireNonNull(Bot.jda.getGuildById(id)).getOwnerId();
////        newGuild.prefix = Bot.properties.getPrefix();
////
////        GuildResponse resp = post(Endpoints.Guilds, GuildResponse.class, newGuild);
////        if (resp != null)
////        {
////            log.debug("Created new Guild Entry {}", resp.get().toString());
////            return resp.get();
////        }
////        return null;
////    }
//
//    /**
//     * Update a guild's entry in the api.
//     * @param guild ApiGuild data to update.
//     * @return The updated ApiGuild object.
//     */
//    public ApiGuild updateGuild(ApiGuild guild)
//    {
//        GuildResponse resp = post(String.format(Endpoints.Guild, guild.snowflake), GuildResponse.class, guild.toString());
//        if (resp != null)
//        {
//            return resp.get();
//        }
//        return null;
//    }





}
