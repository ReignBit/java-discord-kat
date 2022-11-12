package com.reign.kat.lib.voice;

import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifyConfig;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifySourceManager;
import com.reign.kat.lib.Config;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages all Guild audio interactions
 *
 *
 * KatAudio - Main API for interacting with voice
 *
 * GuildAudioManager - Manages voice and track flow for an individual guild
 * VoiceMusicPlayer - System responsible for queueing music and controlling its flow
 * VoiceAssistantPlayer - System responsible for queueing voice responses and their actions
 *
 *
 *
 */
public class KatAudioManager {
    private static final Logger log = LoggerFactory.getLogger(KatAudioManager.class);
    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static final HashMap<String, GuildAudio> guildPlayers = new HashMap<>();

    public KatAudioManager()
    {

        SpotifyConfig spotifyConfig = new SpotifyConfig();
        spotifyConfig.setClientId(Config.VOICE_SPOTIFY_CLIENT_ID);
        spotifyConfig.setClientSecret(Config.VOICE_SPOTIFY_CLIENT_SECRET);
        spotifyConfig.setCountryCode(Config.VOICE_SPOTIFY_COUNTRY_CODE);
        playerManager.registerSourceManager(new SpotifySourceManager(null, spotifyConfig, playerManager));
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public GuildAudio getGuildManager(Guild guild)
    {
        String id = guild.getId();
        if (guildPlayers.containsKey(id))
        {
            log.trace("GUILD MANAGER ALREADY EXISTS. RETURNING");
            return guildPlayers.get(id);
        }
        log.trace("NO GUILD MANAGER FOR {}, CREATING", id);
        GuildAudio gam = createGuildManager(guild);
        guildPlayers.put(id, gam);
        return gam;
    }

    public GuildAudio createGuildManager(Guild guild)
    {
        GuildAudio guildManager = new GuildAudio(guild, playerManager);
        guild.getAudioManager().setSendingHandler(guildManager.getSendHandler());
        guild.getAudioManager().setReceivingHandler(guildManager.getRecvHandler());
        return guildManager;
    }

    public Set<Map.Entry<String, GuildAudio>> all()
    {
        return guildPlayers.entrySet();
    }

    public static void deleteGuildManager(Guild guild)
    {
        guildPlayers.get(guild.getId()).disconnect();
        guildPlayers.remove(guild.getId());
    }

}
