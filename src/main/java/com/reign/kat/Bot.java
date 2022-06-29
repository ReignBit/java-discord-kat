package com.reign.kat;

import com.reign.kat.commands.ExampleCog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.time.Instant;

public class Bot {
    private static final Logger log = LogManager.getLogger(Bot.class);
    private DiscordApi api;
    private static final long startedTimestamp = Instant.now().getEpochSecond();
    private static final String prefix = "!";

    public static String getPrefix() { return prefix; }
    public static long getStartedTimestamp() { return startedTimestamp; }

    public Bot(String token)
    {
        FallbackLoggerConfiguration.setDebug(true);

        new DiscordApiBuilder().setToken(token).setAllIntents().login().thenAccept(api -> {
            log.info("Successfully logged into Discord API.");
            log.info(String.format("LOGIN as %s - %s", api.getYourself().getName(), api.getYourself().getIdAsString()));
            log.info(String.format("Can see %d users across %d servers", api.getCachedUsers().size(), api.getServers().size()));

            ExampleCog exampleCog = new ExampleCog(api);

        });

    }



}
