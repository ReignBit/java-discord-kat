package com.reign.kat.lib.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Audio for a single guild
 */
public class GuildAudioManager {
    private static Logger log;
    public final AudioPlayer player;
    public final TrackScheduler scheduler;

    public final Guild guild;

    public GuildAudioManager(Guild guild, AudioPlayerManager manager) {
        this.guild = guild;

        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);

        log = LoggerFactory.getLogger(String.format("GuildAudioManager_%s", guild.getId()));
        log.info("Created GuildAudioManager for {}", guild.getId());
    }

    public void loadSearch(VoiceChannel channel, String searchQuery)
    {
        log.info("Loading query: {}...", searchQuery);
        KatAudioManager.playerManager.loadItemOrdered(this, searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                log.info("Adding track to queue: {}", track.getInfo().title);
                play(channel, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                {
                    firstTrack = playlist.getTracks().get(0);
                }

                log.info("Adding first track ({}) from playlist: {}", firstTrack.getInfo().title, playlist.getName());
                play(channel, firstTrack);
            }

            @Override
            public void noMatches() {
                log.info("No matches found for query {}", searchQuery);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.warn("Failed to load track: {}", exception.getMessage());
            }
        });
    }

    private void play(VoiceChannel channel, AudioTrack track)
    {
        connectToChannel(channel);
        scheduler.queue(track);
    }

    private void skip()
    {
        log.info("Skipping...");
        scheduler.nextTrack();
    }

    private void connectToChannel(VoiceChannel channel)
    {
        AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected())
        {
            audioManager.openAudioConnection(channel);
        }
    }

    private void moveChannel(VoiceChannel channel)
    {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected())
        {
            audioManager.closeAudioConnection();
        }
        audioManager.openAudioConnection(channel);
    }

    public AudioPlayerSendHandler getSendHandler()
    {
        return new AudioPlayerSendHandler(player);
    }
}
