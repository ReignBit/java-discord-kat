package com.reign.kat.lib.handlers;

import com.reign.kat.Bot;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.utils.stats.BotStats;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import com.reign.kat.lib.voice.newvoice.PlaylistPlayer;
import com.reign.kat.lib.voice.newvoice.RequestedTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ResponseHandler
 */
public class GuildPlaylistResponseHandler extends AudioEventAdapter
{
    private static final Logger log = LoggerFactory.getLogger(GuildPlaylistResponseHandler.class);
    private final PlaylistPlayer player;
    private final long guildID;
    private long textChannelID = 0L;

    private InteractionHook hook;

    private RequestedTrack errorTrack = null;
    private static int totalErrorCount = 0; // Does not get reset by giving up.
    private int errorCount = 0;
    private static int successCount = 0;

    public static float getErrorRate() { return (float)totalErrorCount / ((float)totalErrorCount + (float)successCount); }

    public GuildPlaylistResponseHandler(long guildID, PlaylistPlayer playlistPlayer)
    {
        this.guildID = guildID;
        this.player = playlistPlayer;
        BotStats.addToReport("Music Play Error Rate", GuildPlaylistResponseHandler::getErrorRate);
    }

    public long getTextChannelID()
    {
        return textChannelID;
    }

    public void setTextChannelID(long textChannelID)
    {
        this.textChannelID = textChannelID;
    }

    public void setHook(InteractionHook hook)
    {
        this.hook = hook;
    }

    public void onNoMatches(String searchQuery)
    {
        sendEmbed(new ExceptionEmbed()
                .setTitle("No matches found")
                .setDescription("Couldn't find a video with the title `" + searchQuery + "`").build());
    }

    public void onRequestedTracks(List<RequestedTrack> tracks, PlaylistPlayer player)
    {
        if (tracks.size() == 1 && player.nowPlaying == null)
        {
            return;
        } // We don't want to send if we are only playing the track just requested.

        if (tracks.size() == 1)
        {
            RequestedTrack track = tracks.get(0);

            sendEmbedWithSingleComponent(
                    Button.primary("play-again", "Queue Again"),
                    new VoiceEmbed()
                            .setPausedNotification(player.lavaPlayer.isPaused())
                            .setTitle("Added a track to the queue")
                            .setDescription(track.toString())
                            .build()
            );
        } else
        {
            sendEmbed(
                    new VoiceEmbed()
                            .setPausedNotification(player.lavaPlayer.isPaused())
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
                        .setTitle(":pause_button: Paused the music!")
                        .setDescription("Use `play` to continue playing.")
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
        sendEmbedWithActionRow(
                new ActionRowChildComponent[]{Button.primary("play-again", "Queue Again")},
                new VoiceEmbed()
                        .setTitle("Now playing")
                        .setDescription(track.toString())
                        .build()
        );
        successCount++;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        // TODO: Add tries before stopping music to avoid spamming chat with errors for each track.
        // TODO: Also maybe look into retrying if we have some kind of errors?
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

        if (errorTrack == track)
        {
            errorCount++;
            totalErrorCount++;

            if (errorCount >= PlaylistPlayer.ERROR_LIMIT)
            {
                sendEmbed(new ExceptionEmbed()
                        .setTitle("Giving up attempting to play track [3/3]")
                        .setDescription(String.format(
                                "Error loop detected when trying to play **[%s](%s)**.\nSkipping...",
                                track.title,
                                track.url
                            )
                        )
                        .build()
                );
                errorCount = 0;
                errorTrack = null;
                return;
            }

            GuildPlaylistPool.get(guildID).getQueue().enqueueFront(track);
            errorTrack = track;
            log.warn("PLAYER RETRY: Retrying {} [{}/{}]", track.url, errorCount, PlaylistPlayer.ERROR_LIMIT);
        }

    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace)
    {
        log.error("Track stuck! {}", (Object) stackTrace);
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
                } else
                {
                    channel.sendMessageEmbeds(embed).queue();
                }
            }
        }

        hook = null;
    }

    private void sendEmbedWithSingleComponent(ActionRowChildComponent component, MessageEmbed... embeds)
    {

        sendEmbedWithActionRow(new ActionRowChildComponent[]{component}, embeds);
    }

    private void sendEmbedWithActionRow(ActionRowChildComponent[] components, MessageEmbed... embeds)
    {
        TextChannel channel = Objects.requireNonNull(Bot.jda.getGuildById(guildID)).getTextChannelById(textChannelID);
        if (channel != null)
        {
            MessageCreateData data = new MessageCreateBuilder()
                    .setEmbeds(embeds)
                    .addComponents(ActionRow.of(Arrays.asList(components)))
                    .build();

            if (hook != null)
            {
                hook.sendMessage(data).queue();
            } else
            {
                channel.sendMessage(data).queue();
            }
        }

        hook = null;
    }
}
