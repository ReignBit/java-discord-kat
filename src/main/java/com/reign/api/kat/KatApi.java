package com.reign.api.kat;

import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.models.ApiGuildData;
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

    private final HttpClient client;

    private final String host;
    private final String authStr;

    public KatApi(String host, String authentication) {
        client = HttpClient.newHttpClient();
        this.host = host;
        this.authStr = authentication;

        if (getTestConnection())
        {
            log.info("Connected to KatAPI.");
        }
    }

    /**
     * Sends a GET requst to the api root endpoint to ensure its alive.
     * @return boolean if request was successfully made
     */
    public boolean getTestConnection()
    {
        HelloResponse resp = get("", HelloResponse.class);
        if (resp != null) {
            if (resp.status == 200) {
                return true;
            }
            log.error("Established connection to KatAPI, but failed to get a 200 response. {}", resp.err);
        }
        return false;
    }

    /**
     * Retrieve a Guild from the Api with snowflake = id.
     * @param id Snowflake of the guild
     * @return ApiGuild - Guild data retrieved from api.
     */
    public ApiGuild getGuild(String id)
    {
        GuildResponse resp = get(String.format("%s/%s", Endpoints.Guilds, id), GuildResponse.class);
        if (resp != null && resp.data != null)
        {
            log.debug(resp.get().toString());
            return resp.get();
        }
        return null;
    }

    /**
     * Retrieve a GuildData entry from the Api, creating it in the Api if ensureExists = true
     * @param id Snowflake of the guild
     * @param ensureExists If true, POST request to guilds and create an entry
     *                     for the guild if an entry doesnt already exist
     * @return ApiGuild - GuildData data retrieved from api.
     */
    public ApiGuildData getGuildData(String id, boolean ensureExists)
    {
        GuildDataResponse resp = get(String.format("%s/%s", Endpoints.Guilds, id), GuildDataResponse.class);
        if (resp != null) {
            if (resp.get() == null && ensureExists) {
                // Guild doesn't exist in the Api.
                return createGuildData(id);
            }

            log.debug(resp.get().toString());
            return resp.get();
        }
        return null;
    }


    /**
     * Create a new guildData entry in the api.
     * @param id Snowflake of the guild.
     * @return A new ApiGuildData with the required data loaded.
     */
    public ApiGuildData createGuildData(String id)
    {
        ApiGuildData newGuildData = new ApiGuildData();
        newGuildData.snowflake = id;

        GuildDataResponse resp = post(String.format("%s/%s/data", Endpoints.Guilds, id), GuildDataResponse.class, newGuildData);
        if (resp != null)
        {
            log.debug("Created new Guild Entry {}", resp.get().toString());
            return resp.get();
        }
        return null;
    }

    /**
     * Update a guild's entry in the api.
     * @param guild ApiGuild data to update.
     * @return The updated ApiGuild object.
     */
    public ApiGuildData updateGuildData(ApiGuildData guildData)
    {
        GuildDataResponse resp = post(String.format("%s/%s/data", Endpoints.Guilds, guildData.snowflake), GuildDataResponse.class, guildData.toString());
        if (resp != null)
        {
            return resp.get();
        }
        return null;
    }

    /**
     * Retrieve a Guild from the Api, creating it in the Api if ensureExists = true
     * @param id Snowflake of the guild
     * @param ensureExists If true, POST request to guilds and create an entry
     *                     for the guild if an entry doesnt already exist
     * @return ApiGuild - Guild data retrieved from api.
     */
    public ApiGuild getGuild(String id, boolean ensureExists)
    {
        GuildResponse resp = get(String.format(Endpoints.Guild, id), GuildResponse.class);
        if (resp != null) {
            if (resp.get() == null && ensureExists) {
                // Guild doesn't exist in the Api.
                return createGuild(id);
            }

            log.debug(resp.get().toString());
            return resp.get();
        }
        return null;
    }

    public String getGuildPrefix(String id, boolean ensureExists)
    {
        GuildPrefixResponse resp = get(String.format(Endpoints.GuildPrefix, id), GuildPrefixResponse.class);
        if (resp != null)
        {
            if (resp.get() == null && ensureExists)
            {
                return createGuild(id).getPrefix();
            }

            return resp.data;
        }
        log.error("Something horribly wrong has happened when trying to request the prefix for guild ${id}");
        return null;
    }

    /**
     * Gets all guilds from the Api.
     * @return ArrayList type ApiGuild
     */
    public ArrayList<ApiGuild> getGuilds() {
        GuildsResponse resp = get(Endpoints.Guilds, GuildsResponse.class);
        if (resp != null)
        {
            return resp.guilds();
        }
        return new ArrayList<>();
    }

    /**
     * Create a new guild entry in the api.
     * @param id Snowflake of the guild.
     * @return A new ApiGuild with the required data loaded.
     */
    public ApiGuild createGuild(String id)
    {
        ApiGuild newGuild = new ApiGuild();
        newGuild.snowflake = id;
        newGuild.discoveredAt = Instant.now().getEpochSecond();
        newGuild.ownerId = Objects.requireNonNull(Bot.jda.getGuildById(id)).getOwnerId();
        newGuild.prefix = Bot.properties.getPrefix();

        GuildResponse resp = post(Endpoints.Guilds, GuildResponse.class, newGuild);
        if (resp != null)
        {
            log.debug("Created new Guild Entry {}", resp.get().toString());
            return resp.get();
        }
        return null;
    }

    /**
     * Update a guild's entry in the api.
     * @param guild ApiGuild data to update.
     * @return The updated ApiGuild object.
     */
    public ApiGuild updateGuild(ApiGuild guild)
    {
        GuildResponse resp = post(String.format(Endpoints.Guild, guild.snowflake), GuildResponse.class, guild.toString());
        if (resp != null)
        {
            return resp.get();
        }
        return null;
    }


    /**
     * Base method for all GET requests.
     * @param endpoint URI to request.
     * @param response JSON-Serializable class which the response will be transformed into.
     * @return
     * @param <T>
     */
    private <T> T get(String endpoint, Class<T> response) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(String.format("%s/%s", host, endpoint))
                )
                .setHeader("Authorization", authStr)
                .build();

        log.debug("GET Requesting {}", String.format("%s/%s", host, endpoint));
        try
        {
            HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

            // Ok status codes - Client Error status codes
            if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                log.trace("Status code {}", resp.statusCode());
                return resp.body().get();
            }
            log.warn("Non-Ok status code ({}) received from POST {}", resp.statusCode(), String.format("%s%s", host, endpoint));
        } catch (ExecutionException | InterruptedException e)
        {
            log.error(String.valueOf(e));
            log.error("An error occurred whilst trying to GET request {}/{}", host, endpoint);
        }
        return null;
    }

    /**
     * Base method for all POST requests
     * @param endpoint Request URI.
     * @param response Class in which the response will be serialized into.
     * @param body JSON-Serializable object containing the body data.
     * @return Generic T; Serialized JSON data into an object of type T response.
     * @param <T> T JSON-Serializable class which the response will be serialized into.
     * @param <Y> Y JSON-Serializable object which the request body will be transformed from.
     */
    private <T,Y> T post(String endpoint, Class<T> response, Y body) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(String.format("%s/%s", host, endpoint))
                )
                .setHeader("Authorization", authStr)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        log.debug("POST Requesting {}", String.format("%s%s", host, endpoint));
        log.trace("body: {}", body);
        try
        {
            HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

            // Ok status codes - Client Error status codes
            if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                log.trace("Status code {}", resp.statusCode());
                return resp.body().get();
            }
            log.warn("Non-Ok status code ({}) received from POST {}", resp.statusCode(), String.format("%s%s", host, endpoint));
        } catch (ExecutionException | InterruptedException e)
        {
            log.error("An error occurred whilst trying to POST request {}/{}", host, endpoint);
        }
        return null;
    }


}
