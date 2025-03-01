package com.reign.kat.lib.voice.newvoice;

import com.reign.kat.Bot;
import com.reign.kat.lib.Config;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

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
        //YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true, new Web(), new Music(), new TvHtml5Embedded(), new WebEmbedded(), new AndroidMusic());
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true, new Web());
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

    public static void loadPersistData()
    {
        log.debug("Loading from persist file...");
        // TODO: Replace this with something more conventional? Like json or smth.
        try {
            BufferedReader buf = new BufferedReader(new FileReader("persist"));

            long guild = 0;
            while(buf.ready())
            {
                String line = buf.readLine();
                if (line.startsWith("guild:")) {
                    // Start of new guild
                    guild = Long.parseLong(line.split(":")[1]);
                    GuildPlaylistPool.get(guild);
                }
                else if (line.startsWith("vc")) {
                    long id = Long.parseLong(line.split(":")[1]);
                    GuildPlaylistPool.get(guild).connectIfNotConnected(Bot.jda.getVoiceChannelById(id));
                }
                else if (line.startsWith("tc")) {
                    long id = Long.parseLong(line.split(":")[1]);
                    GuildPlaylistPool.get(guild).getResponseHandler().setTextChannelID(id);
                }
                else if (line.startsWith("np")) {
                    String[] splits = line.split(",");

                    Member requester = Objects.requireNonNull(Bot.jda.getGuildById(guild)).getMemberById(splits[1]);
                    
                    //GuildPlaylistPool.get(guild).;
                }
            }

        } catch (IOException e) {
            log.error("Failed to read persist data, reason: {}", e.getLocalizedMessage());
        }
    }

    public static void savePersistData()
    {
        log.debug("Hello from shutdown hook!");
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter("persist"));

            for (Iterator<Map.Entry<Long, GuildPlaylist>> it = GuildPlaylistPool.all(); it.hasNext(); ) {
                GuildPlaylist gp = it.next().getValue();
                long id = gp.guildID;


                log.debug("Persisting {}", id);
                List<RequestedTrack> tracks = gp.getQueue().getQueue();

                buf.write(String.format("guild:%d\n", id));
                buf.write(String.format("vc:%d\n", gp.getVoiceChannel().getIdLong()));
                buf.write(String.format("tc:%d\n", gp.getLastTextChannel().getIdLong()));
                RequestedTrack np = gp.nowPlaying();
                if (np != null) {
                    buf.write(String.format("np:%s,%s,%s\n", np.url, np.requester.id, np.track.getPosition()));
                }

                for(RequestedTrack t : tracks) {
                    buf.write(String.format("track:%s,%s\n", t.url, t.requester.id));
                }
            }

            buf.flush();
            buf.close();
        } catch (IOException e) {
            log.error("Failed to write persist data, reason: {}", e.getLocalizedMessage());
        }
    }

}
