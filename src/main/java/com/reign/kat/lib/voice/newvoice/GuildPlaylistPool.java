package com.reign.kat.lib.voice.newvoice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.Android;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.TvHtml5Embedded;
import dev.lavalink.youtube.clients.Web;
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
        // All source managers get initialized here.
        playerManager.registerSourceManager(SpotifyRemoteSource.build(playerManager));
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true, new Web(), new Music(), new TvHtml5Embedded()));
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
