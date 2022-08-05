package com.reign.api.kat;

import com.reign.api.kat.responses.ApiGuild;
import com.reign.api.kat.responses.GuildResponse;
import com.reign.api.lib.JsonBodyHandler;
import com.reign.api.kat.responses.GuildsResponse;
import com.reign.api.kat.responses.HelloResponse;
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
     * @param id
     * @return
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
     * Retrieve a Guild from the Api, creating it in the Api if ensureExists = true
     * @param id
     * @param ensureExists
     * @return
     */
    public ApiGuild getGuild(String id, boolean ensureExists)
    {
        GuildResponse resp = get(String.format("%s/%s", Endpoints.Guilds, id), GuildResponse.class);
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

    private <T> T get(String endpoint, Class<T> response) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(String.format("%s/%s", host, endpoint))
                )
                .setHeader("Authorization", authStr)
                .build();

        log.debug("GET Requesting {}", String.format("%s%s", host, endpoint));
        try
        {
            HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

            // Ok status codes - Client Error status codes
            if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                log.debug("Status code {}", resp.statusCode());
                return resp.body().get();
            }
            log.warn("Non-Ok status code ({}) received from POST {}", resp.statusCode(), String.format("%s%s", host, endpoint));
        } catch (ExecutionException | InterruptedException e)
        {
            log.error("An error occurred whilst trying to GET request {}/{}", host, endpoint);
        }
        return null;
    }

    private <T,Y> T post(String endpoint, Class<T> response, Y body) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(String.format("%s/%s", host, endpoint))
                )
                .setHeader("Authorization", authStr)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        log.debug("POST Requesting {}, body: {}", String.format("%s%s", host, endpoint), body.toString());
        try
        {
            HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

            // Ok status codes - Client Error status codes
            if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                log.debug("Status code {}", resp.statusCode());
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
