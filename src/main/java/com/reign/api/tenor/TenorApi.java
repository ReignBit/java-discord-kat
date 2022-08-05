package com.reign.api.tenor;

import com.reign.api.lib.JsonBodyHandler;
import com.reign.api.tenor.responses.TenorGifs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class TenorApi{
    public static final int GIF_TTL = 3600;
    private static final Logger log = LoggerFactory.getLogger(TenorApi.class);

    private static final String host = "https://api.tenor.com/v1";

    private final HttpClient client;
    private final String apiKey;
    private final String anonKey;

    static HashMap<String, TenorGifs> gifCache = new HashMap<>();

    public TenorApi(String apiKey, String anonKey)
    {
        client = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.anonKey = anonKey;
    }

    private TenorGifs fetchFromCache(String key)
    {
        if (gifCache.containsKey(key))
        {
            TenorGifs item = gifCache.get(key);
            if (Instant.now().toEpochMilli() - item.requestedAt > TenorApi.GIF_TTL)
            {
                gifCache.remove(key);
            }
            return item;
        }
        return null;
    }

    public TenorGifs get(String endpoint, String q)
    {
        if (gifCache.containsKey(endpoint + q))
        {
            log.debug("Cache hit!");
            return fetchFromCache(endpoint + q);
        }

        log.debug("Cache miss!");
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(host + endpoint + String.format("?q=%s&key=%s&limit=20&anon_id=%s", q, apiKey, anonKey))
        ).build();

        try
        {
            gifCache.put(endpoint + q, client.sendAsync(request, new JsonBodyHandler<>(TenorGifs.class)).get().body().get());
            return gifCache.get(endpoint + q);
        } catch(InterruptedException | ExecutionException e)
        {
            log.warn("Failed to execute TenorGifs.get. {}", e.toString());
        }
        return null;
    }
}
