package com.reign.kat.lib.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Manages all Guild audio interactions
 */
public class KatAudioManager {
    private static final Logger log = LoggerFactory.getLogger(KatAudioManager.class);
    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static final HashMap<String, GuildAudioManager> guildPlayers = new HashMap<>();

    public KatAudioManager()
    {
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public GuildAudioManager getGuildManager(Guild guild)
    {
        String id = guild.getId();
        if (guildPlayers.containsKey(id))
        {
            log.trace("GUILD MANAGER ALREADY EXISTS. RETURNING");
            return guildPlayers.get(id);
        }
        log.trace("NO GUILD MANAGER FOR {}, CREATING", id);
        GuildAudioManager gam = createGuildManager(guild);
        guildPlayers.put(id, gam);
        return gam;
    }

    public GuildAudioManager createGuildManager(Guild guild)
    {
        GuildAudioManager guildManager = new GuildAudioManager(guild, playerManager);
        guild.getAudioManager().setSendingHandler(guildManager.getSendHandler());
        return guildManager;
    }

}
