package com.reign.api.sponsorblock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.reign.api.kat.ApiCache;

import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class SponsorblockAPI {
    private static final Logger log = LoggerFactory.getLogger(SponsorblockAPI.class);
    private static final HttpClient http = HttpClient.newHttpClient();
    private static final String API_URL = "https://sponsor.ajay.app/api";

    private static final ApiCache<SponsorblockVideoData> cache = new ApiCache<>(SponsorblockVideoData.class, Duration.ofDays(1));


    public static SkipSegment[] getSkipSegments(String videoID) {
        SponsorblockVideoData hit = cache.get(videoID);
        if (hit != null) {
            return hit.segments;
        }

        SponsorblockVideoData data = search(videoID);
        if (data != null) {
            cache.upsert(videoID, data);
            return data.segments;
        }

        // No sponsorblock data for video id.
        return null;
    }

    private static SponsorblockVideoData search(String videoID) {

        String url = String.format(
                "%s/skipSegments?videoID=%s&categories=[%s]",
                API_URL,
                videoID,
                URLEncoder.encode("\"music_offtopic\",\"sponsor\",\"intro\",\"outro\"", StandardCharsets.UTF_8)
        );
        log.debug(url);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url)).build();

        try {
            HttpResponse<String> response = http.sendAsync(req, HttpResponse.BodyHandlers.ofString()).get();

            if (response.statusCode() == 404) {
                log.debug("No skip segments for {}", videoID);
                return null;
            } else if (response.statusCode() != 200) {
                log.warn("Non-OK status code for {} - status {}", videoID, response.statusCode());
                return null;
            }

            JsonArray data = JsonParser.parseString(response.body()).getAsJsonArray();

            SkipSegment[] segments = new SkipSegment[data.size()];

            for (int i = 0; i < data.size(); i++) {
                JsonObject d = data.get(i).getAsJsonObject();
                float start = d.get("segment").getAsJsonArray().get(0).getAsFloat();
                float stop = d.get("segment").getAsJsonArray().get(1).getAsFloat();
                segments[i] = new SkipSegment(videoID, start, stop);
            }

            return new SponsorblockVideoData(videoID, segments);

        } catch (ExecutionException | InterruptedException e) {
            log.warn("Failed to get {} - error code {}", url, e.getMessage());
            return null;
        }
    }
}
