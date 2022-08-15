package com.reign.kat.lib.voice;

import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Audio for a single guild
 */
public class GuildAudioManager {
    private static Logger log;
    public final AudioPlayer player;
    public final TrackScheduler scheduler;

    public final Guild guild;

    public GuildMessageChannel lastTextChannel;
    public Message lastMessage = null;

    public GuildAudioManager(Guild guild, AudioPlayerManager manager) {
        this.guild = guild;

        player = manager.createPlayer();

        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);

        log = LoggerFactory.getLogger(String.format("GuildAudioManager_%s", guild.getId()));
        log.debug("Created GuildAudioManager for {}", guild.getId());
    }

    public void setTextChannel(GuildMessageChannel channel)
    {
        lastTextChannel = channel;
    }

    public void loadSearch(VoiceChannel channel, String searchQuery, Member requester)
    {
        log.info("Loading query: {}...", searchQuery);
        String finalSearchQuery = getLavalinkSearchQuery(searchQuery);
        log.info(finalSearchQuery);

        KatAudioManager.playerManager.loadItemOrdered(this, finalSearchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                RequestedTrack t = new RequestedTrack(requester, track);
                if (scheduler.isPlaying())
                {
                    onTrackAddedToQueue(t);
                }
                play(channel, t);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                log.info(String.valueOf(playlist.getTracks().size()));
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (playlist.isSearchResult())
                {
                    // Just get the first result only.
                    RequestedTrack track = new RequestedTrack(requester, firstTrack);
                    if (scheduler.isPlaying())
                    {
                        onTrackAddedToQueue(track);
                    }
                    play(channel, track);
                }
                else
                {
                    // Is an actual playlist.
                    ArrayList<RequestedTrack> tracks = new ArrayList<>();
                    for (AudioTrack track:
                         playlist.getTracks()) {
                        RequestedTrack t = new RequestedTrack(requester, track);
                        tracks.add(t);
                        play(channel, t);
                    }
                    onPlaylistAddedToQueue(tracks);
                }
            }

            @Override
            public void noMatches() {
                log.info("No matches found for query {}", finalSearchQuery);
                onNoMatchesFound();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.warn("Failed to load track: {}", exception.getMessage());
                onFailedToLoadTrack(exception);
            }
        });
    }

    private void onFailedToLoadTrack(FriendlyException exception)
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }
        EmbedBuilder eb = new ExceptionEmbedBuilder(":x:",
                "Failed to load track",
                "The track failed to load, this is normally due to the video being unavailable.\n" + exception.getMessage());
        lastTextChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private void onNoMatchesFound()
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("No matches found")
                .setDescription("No videos could be found for your search.");
        lastTextChannel.sendMessageEmbeds(eb.build()).queue();
    }
    private void onTrackAddedToQueue(RequestedTrack track)
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Added a track to the queue")
                .setDescription(
                        String.format(
                                "**%s**\n%s Requested by: %s",
                                track.getTrack().getInfo().title,
                                timeConversion(track.getTrack().getDuration()),
                                track.getRequester().getAsMention()
                        )
                );
        lastTextChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private void onPlaylistAddedToQueue(ArrayList<RequestedTrack> tracks)
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle(String.format("Added %d tracks to the queue", tracks.size()));
        lastTextChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private void onSkipTrack()
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Skipped song!");
        lastTextChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private String getLavalinkSearchQuery(String searchQuery) {
        log.trace("WE GOT '{}'", searchQuery);
        if (searchQuery.startsWith("http")) {
            // Links do not need any prefix - just return the link.
            return searchQuery;
        }
        return String.format("ytsearch: %s", searchQuery);
    }

    private void play(VoiceChannel channel, RequestedTrack track)
    {
        connectToChannel(channel);
        scheduler.setTextChannel(lastTextChannel);
        scheduler.queue(track);
    }

    public void skip()
    {
        onSkipTrack();
        scheduler.setTextChannel(lastTextChannel);
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

    public static String timeConversion(Long millie) {
        if (millie != null) {
            long seconds = (millie / 1000);
            long sec = seconds % 60;
            long min = (seconds / 60) % 60;
            long hrs = (seconds / (60 * 60)) % 24;
            if (hrs > 0) {
                return String.format("%02d:%02d:%02d", hrs, min, sec);
            } else {
                return String.format("%02d:%02d", min, sec);
            }
        } else {
            return null;
        }
    }

    public static Long stringToTimeConversion(String timestamp) {
        List<String> times = new ArrayList<>(Arrays.stream(timestamp.split(":")).toList());
        times.sort(Collections.reverseOrder());

        // 0 => seconds, 1 => minutes

        if (times.size() == 0)
        {
            return -1L;
        }

        long seconds = Long.parseLong(times.get(0)) * 1000;

        long minutes = 0L;
        if (times.size() > 1)
        {
            minutes = Long.parseLong(times.get(1)) * 60000;
        }

        log.debug("Position: {}", seconds + minutes);
        return seconds + minutes;

    }
}
