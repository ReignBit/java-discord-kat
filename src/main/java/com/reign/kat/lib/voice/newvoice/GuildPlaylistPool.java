package com.reign.kat.lib.voice.newvoice;

import com.reign.kat.lib.Config;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GuildPlaylistPool
{
    private static final Logger log = LoggerFactory.getLogger(GuildPlaylistPool.class);

    private static final HashMap<Long, GuildPlaylist> guildPlayers = new HashMap<>();
    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();


    /**
     * Initialize player settings and register remote audio sources.
     */
    public static void init()
    {
        // Warn if we don't have good state for YT
        if (Config.YT_PO_TOKEN.isEmpty() || Config.YT_VISITOR_DATA.isEmpty()) {
            log.warn("Config `yt-po-token` or `yt-visitor-data` is missing! YT may not work properly!");
        }

        // All source managers get initialized here.
        playerManager.registerSourceManager(SpotifyRemoteSource.build(playerManager));
        Web.setPoTokenAndVisitorData(Config.YT_PO_TOKEN, Config.YT_VISITOR_DATA);

        ClientOptions webOptions = new ClientOptions();
        webOptions.setPlayback(false);

        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true, new MWeb(), new Web(webOptions), new Music(), new TvHtml5Embedded(), new WebEmbedded(), new AndroidMusic());
        //YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true, new Web());
        playerManager.registerSourceManager(youtube);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    /**
     * Get a GuildPlaylist for requested guild ID
     * @param guildId long GuildID from discord
     * @return a GuildPlaylist instance (one is created if the pool does not have an instance for the id)
     */
    public static GuildPlaylist get(long guildId)
    {
        if (!guildPlayers.containsKey(guildId))
        {
            // GuildPlaylist doesn't exist for this guild.
            guildPlayers.put(guildId, new GuildPlaylist(guildId, playerManager));
            log.debug("Created GuildPlaylist for {}", guildId);
        }
        return guildPlayers.get(guildId);
    }

    public static Iterator<Map.Entry<Long,GuildPlaylist>> all()
    {
        return guildPlayers.entrySet().iterator();
    }

    /**
     * Remove a Guild's GuildPlaylist instance.
     * This also calls `GuildPlaylist.destroy()` on the instance
     * @param guildId long GuildID from discord
     */
    public static void remove(long guildId)
    {
        if (guildPlayers.containsKey(guildId))
        {
            guildPlayers.get(guildId).destroy();
            guildPlayers.remove(guildId);
        }
    }

}
