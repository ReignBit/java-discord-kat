package com.reign.kat.lib.handlers;

import com.reign.kat.Bot;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.newvoice.PlaylistPlayer;
import com.reign.kat.lib.voice.newvoice.RequestedTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.MessageEmbed;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * ResponseHandler
 *
 *
 */
public class GuildPlaylistResponseHandler extends AudioEventAdapter
{
    private static final Logger log = LoggerFactory.getLogger(GuildPlaylistResponseHandler.class);
    private final PlaylistPlayer player;
    private final long guildID;
    private long textChannelID = 0L;

    private InteractionHook hook;

    public GuildPlaylistResponseHandler(long guildID, PlaylistPlayer playlistPlayer)
    {
        this.guildID = guildID;
        this.player = playlistPlayer;
    }

    public void setTextChannelID(long textChannelID)
    {
        this.textChannelID = textChannelID;
    }
    public long getTextChannelID() { return textChannelID; }

    public void setHook(InteractionHook hook)
    {
        this.hook = hook;
    }

    private void sendEmbed(MessageEmbed... embeds)
    {

        TextChannel channel = Objects.requireNonNull(Bot.jda.getGuildById(guildID)).getTextChannelById(textChannelID);
        if (channel != null)
        {
            for (MessageEmbed embed :
                    embeds)
            {
                if (hook != null)
                {
                    hook.sendMessageEmbeds(embed).queue();
                }else
                {
                    channel.sendMessageEmbeds(embed).queue();
                }
            }
        }

        hook = null;
    }

    public void onNoMatches(String searchQuery)
    {
        sendEmbed(new ExceptionEmbed()
                .setTitle("No matches found")
                .setDescription("Couldn't find a video with the title `"+ searchQuery +"`").build());
    }

    public void onRequestedTracks(List<RequestedTrack> tracks, PlaylistPlayer player)
    {
        if (tracks.size() == 1 && player.nowPlaying == null) { return; } // We don't want to send if we are only playing the track just requested.

        if (tracks.size() == 1)
        {
            RequestedTrack track = tracks.get(0);
            sendEmbed(
                    new VoiceEmbed()
                            .setTitle("Added a track to the queue")
                            .setDescription(track.toString())
                            .build()
            );
        }
        else
        {
            sendEmbed(
                    new VoiceEmbed()
                            .setTitle(String.format("Added **%d** tracks to the queue", tracks.size()))
                            .build()
            );
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player)
    {
        sendEmbed(
                new VoiceEmbed()
                .setTitle("Paused!")
                .setDescription("Paused the current track. Use `resume` to resume the track!")
                .build()
        );
    }

    @Override
    public void onPlayerResume(AudioPlayer player)
    {
        sendEmbed(
                new VoiceEmbed()
                        .setTitle("Resumed!")
                        .setDescription("Enjoy the music!")
                        .build()
        );
    }

    @Override
    public void onTrackStart(AudioPlayer _player, AudioTrack _track)
    {
        RequestedTrack track = player.nowPlaying;
        sendEmbed(
                new VoiceEmbed()
                        .setTitle("Now playing")
                        .setDescription(track.toString())
                        .build()
        );
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {

    }

    @Override
    public void onTrackException(AudioPlayer _player, AudioTrack _track, FriendlyException exception)
    {
        RequestedTrack track = player.nowPlaying;
        sendEmbed(
                new ExceptionEmbed()
                        .setTitle("Failed to play a track")
                        .setDescription(String.format("Something went wrong when trying to play **[%s](%s)**!\n```\n%s```", track.title, track.url, exception.getMessage()))
                        .build()
        );
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace)
    {
        log.error("Track stuck! {}", (Object) stackTrace);
    }
}
