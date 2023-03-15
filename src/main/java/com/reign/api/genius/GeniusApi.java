package com.reign.api.genius;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class GeniusApi
{
    private static final Logger log = LoggerFactory.getLogger(GeniusApi.class);
    private static final HttpClient http = HttpClient.newHttpClient();
    private static final String GENIUS_SEARCH_URL = "https://genius.com/api/search/multi?q=";

    private static final Map<String, String> cleanupTargets = Map.ofEntries(
            Map.entry("<br><br>", "\n"),
            Map.entry("<br>", ""),
            Map.entry("<i>", " *"),
            Map.entry("</i>", "* "),
            Map.entry("<em>", " *"),
            Map.entry("</em>", "* "),
            Map.entry("<b>", " **"),
            Map.entry("</b>", "** "),
            Map.entry("<strong>", " **"),
            Map.entry("</strong>", "** "),
            Map.entry("<u>", " __"),
            Map.entry("</u>", "__ "),
            Map.entry("&amp;", "&"),
            Map.entry("&lt;", "<"),
            Map.entry("&gt;", ">")
    );


    /**
     * Attempts to search for lyrics for the given song name from Genius.com
     * **!! Results may not be accurate depending on given search term !!**
     * @param songName term to search genius.com for
     * @return String containing lyrics
     */
    public static GeniusSong lyrics(String songName)
    {
        // response.sections[1].hits
        JsonArray hitsArr = Objects.requireNonNull(search(songName)).getAsJsonObject("response")
                .getAsJsonArray("sections")
                .get(1).getAsJsonObject().getAsJsonArray("hits");

        if (hitsArr.size() > 0)
        {
            // We have found a song!
            JsonObject songData = hitsArr.get(0).getAsJsonObject().getAsJsonObject("result");

            // TODO: Please find a better way to do these replacements
            String lyricsFromPage = scrapePageForLyrics(songData.get("url").getAsString());
            if (lyricsFromPage != null)
            {
                lyricsFromPage = cleanupLyrics(lyricsFromPage);
            }

            return new GeniusSong(
                    songData.get("path").getAsString(),
                    songData.get("title").getAsString(),
                    songData.get("artist_names").getAsString(),
                    lyricsFromPage
            );
        }

        // No hit :(
        return null;
    }

    /** Scrapes the given url for lyrics using elements from genius.com
     * (Last updated: 2023/03/13)
     * @param url Url to scrape
     * @return String lyrics scraped
     */
    private static String scrapePageForLyrics(String url)
    {
        if (!url.contains("genius.com"))
        {
            log.warn("Tried to scrape a non genius.com url! Aborting!");
            return "";
        }

        try
        {

            Document page = Jsoup.connect(url)
                    .userAgent("PostmanRuntime/7.29.2")
                    .header("Cache-Control", "no-cache")
                    .header("Accept", "*/*").get();

            Safelist whitelist = Safelist.simpleText().addTags("br");
            return Jsoup.clean(page.selectXpath("//div[@data-lyrics-container]").toString(), whitelist);
        }
        catch (IOException e)
        {
            log.warn("Failed to retrieve webpage {}\n", url, e);
        }

        return null;
    }

    private static JsonObject search(String searchterm)
    {



        URI encodedUrl = URI.create(String.format("%s%s", GENIUS_SEARCH_URL, URLEncoder.encode(searchterm, StandardCharsets.UTF_8)));
        HttpRequest request = HttpRequest.newBuilder(
                encodedUrl
        ).build();

        try
        {
            HttpResponse<String> resp = http.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            return JsonParser.parseString(resp.body()).getAsJsonObject();
        }
        catch (ExecutionException | InterruptedException e )
        {
            log.warn("Failed to request Genius api search at endpoint: {}, error: {}", request.uri(), e);
        }
        return null;
    }

    private static String cleanupLyrics(@NotNull String text)
    {
        return StringUtils.replaceEach(text, cleanupTargets.keySet().toArray(new String[0]), cleanupTargets.values().toArray(new String[0]));
    }
}
