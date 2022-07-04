package com.reign.api;

import com.reign.api.bodyhandlers.JsonBodyHandler;
import com.reign.api.responses.kat.GuildsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class KatApi {
    private static final Logger log = LoggerFactory.getLogger(KatApi.class);

    private final HttpClient client;

    private final String host;
    private final String authStr;

    public KatApi(String host, String authentication)
    {
        client = HttpClient.newHttpClient();
        this.host = host;
        this.authStr = authentication;
    }
    /*
        /guilds ----- List<GuildResponse> GuildResponse.ids
        /users ------ List<

     */



    private <T> T get(String endpoint, Class<T> response) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format("%s/%s", host, endpoint))
        )
                .setHeader("Authorization", authStr)
                .build();

        log.info("Requesting {}", String.format("%s%s", host, endpoint));
        HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

        // Ok status codes - Client Error status codes
        if (resp.statusCode() >= 200 && resp.statusCode() < 400)
        {
            log.info("Status code {}", resp.statusCode());
            return resp.body().get();
        }
        log.warn("Non-Ok status code ({}) received from {}", resp.statusCode(), String.format("%s%s", host, endpoint));
        return null;
    }

    public GuildsResponse getGuildIds()
    {
        try
        {
            return get("/guilds", GuildsResponse.class);

        } catch(ExecutionException | InterruptedException e)
        {
            log.error("Failed to GET /guilds {}", e);
            return null;
        }
    }

}
