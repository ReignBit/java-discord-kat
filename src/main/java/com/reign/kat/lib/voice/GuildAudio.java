package com.reign.kat.lib.voice;

import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.utils.Utilities;
import com.reign.kat.lib.voice.receive.VoiceRecognition;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Audio for a single guild
 */
@Deprecated
public class GuildAudio
{
    private static Logger log;
    private VoiceStatus status;

    public final AudioPlayer player;
    public TrackScheduler scheduler;

    public final Guild guild;

    public GuildMessageChannel lastTextChannel;
    public Message lastMessage = null;

    //public VoiceRecognition voiceRecognition = new VoiceRecognition();
    public VoiceRecognition voiceRecognition = null;

    public GuildAudio(Guild guild, AudioPlayerManager manager) {
        this.guild = guild;
        this.status = VoiceStatus.NOT_INITIALIZED;

        player = manager.createPlayer();

        scheduler = new TrackScheduler(player, this);
        player.addListener(scheduler);
        player.setVolume(50);

        log = LoggerFactory.getLogger(String.format("GuildAudioManager_%s", guild.getId()));
        log.debug("Created GuildAudioManager for {}", guild.getId());
    }


    public void setTextChannel(GuildMessageChannel channel)
    {
        lastTextChannel = channel;
    }

    public void loadSearch(VoiceChannel channel, String searchQuery, Member requester)
    {
        log.debug("Loading query: {}...", searchQuery);
        String finalSearchQuery = getLavalinkSearchQuery(searchQuery);
        log.debug(finalSearchQuery);

        KatAudioManager.playerManager.loadItemOrdered(this, finalSearchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                status = VoiceStatus.LOADING_TRACK;
                RequestedTrack t = new RequestedTrack(requester, track);
                if (scheduler.isPlaying())
                {
                    onTrackAddedToQueue(t, scheduler.queue.size());
                }
                play(channel, t);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                status = VoiceStatus.LOADING_PLAYLIST;
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
                        onTrackAddedToQueue(track, scheduler.queue.size());
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
                status = VoiceStatus.LOAD_TRACK_ERROR;
                log.warn("Failed to load track: {}", exception.getMessage());
                onFailedToLoadTrack(exception);
            }
        });
    }

    public VoiceStatus getStatus() { return status; }
    public void setStatus(VoiceStatus vs) { this.status = vs; }
    public void autoDisconnect()
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }
        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Auto Disconnecting")
                .setDescription("Leaving voice chat since no tracks have been played in 5 minutes.");
        lastTextChannel.sendMessageEmbeds(eb.build()).queue();
        KatAudioManager.deleteGuildManager(guild);
        guild.getAudioManager().closeAudioConnection();
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
    private void onTrackAddedToQueue(RequestedTrack track, int position)
    {
        if (lastTextChannel == null) {
            log.warn("Tried to send a message but lastTextChannel is null!");
            return;
        }

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle(String.format("Added a track to the queue (%d)", position))
                .setDescription(
                        String.format(
                                "**[%s](%s)**\n%s Requested by: %s",
                                track.getTrack().getInfo().title,
                                track.getTrack().getInfo().uri,
                                Utilities.timeConversion(track.getTrack().getDuration()),
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
            status = VoiceStatus.JOINED_CHANNEL;
        }
    }

    public void disconnect()
    {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected())
        {
            audioManager.closeAudioConnection();
            scheduler = new TrackScheduler(player, this);

        }
        status = VoiceStatus.NOT_INITIALIZED;
        KatAudioManager.deleteGuildManager(guild);
    }

    public void connect(VoiceChannel channel)
    {
        moveChannel(channel);
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
