package com.reign.kat.lib.voice;

import com.reign.kat.Bot;
import com.reign.kat.lib.Config;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.reign.kat.lib.utils.Utilities.timeConversion;


public class TrackScheduler extends AudioEventAdapter  {
    private static final Logger log = LoggerFactory.getLogger(TrackScheduler.class);
    private final AudioPlayer player;
    public final ArrayList<RequestedTrack> queue;
    private RequestedTrack nowPlaying;
    private GuildMessageChannel lastTextChannel;
    private Message lastMessage;

    private ScheduledFuture<?> autoDisconnectTimer;

    private final GuildAudio guildAudio;

    public TrackScheduler(AudioPlayer player, GuildAudio guildAudio)
    {
        this.player = player;
        this.queue = new ArrayList<>();
        this.guildAudio = guildAudio;
    }

    public void setTextChannel(GuildMessageChannel channel)
    {
        lastTextChannel = channel;
    }

    public boolean isPlaying()
    {
        return nowPlaying != null;
    }

    public RequestedTrack getNowPlaying()
    {
        return nowPlaying;
    }

    public void queue(RequestedTrack track)
    {

        if (autoDisconnectTimer != null)
        {
            // Cancel any auto disconnect timer before queuing tracks.
            autoDisconnectTimer.cancel(true);
            log.info("Cancelled auto disconnect timer for : {}", guildAudio.guild.getId());
            log.debug(String.valueOf(autoDisconnectTimer.isCancelled()));
        }

        if (!player.startTrack(track.getTrack(), true))
        {

            queue.add(track);
        }
        else
        {
            nowPlaying = track;
            onSendNowPlayingMessage(track);
        }
    }

    public void nextTrack()
    {
        if (queue.size() > 0)
        {
            RequestedTrack next = queue.remove(0);
            nowPlaying = next;
            try
            {
                player.startTrack(next.getTrack(), false);
            }
            catch (Exception e)
            {
                log.debug("Failed to queue track. Trying again...");
                player.startTrack(next.getTrack(), false);
            }
            if (autoDisconnectTimer != null)
                autoDisconnectTimer.cancel(true);
            onSendNowPlayingMessage(nowPlaying);
            return;
        }
        nowPlaying = null;
        lastMessage = null;
        player.stopTrack();

        //autoDisconnectTimer = Bot.executorService.schedule(this::onAutoDisconnect, Config.VOICE_AUTODISCONNECT_MINUTES, TimeUnit.MINUTES);
        log.debug("started auto disconnect timer for: {}", guildAudio.guild.getId());
    }

    public ArrayList<RequestedTrack> getQueue()
    {
        return queue;
    }


    public void onAutoDisconnect()
    {
        guildAudio.autoDisconnect();
        // Cancel the timer in case another one is still running, for whatever reason.
        //autoDisconnectTimer.cancel(true);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        super.onTrackException(player, track, exception);
        log.warn(exception.toString());
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext)
        {
            nextTrack();
        }
        else
        {
            nowPlaying = null;
            lastMessage = null;
        }
        log.debug("Finished track");
    }

    private void onSendNowPlayingMessage(RequestedTrack track)
    {
        if (lastTextChannel == null) {
            return;
        }
        try
        {
            EmbedBuilder eb = new VoiceEmbed()
                    .setTitle("Now Playing")
                    .setDescription(
                            String.format(
                                    "**%s**\n%s Requested by: %s",
                                    track.getTrack().getInfo().title,
                                    timeConversion(track.getTrack().getDuration()),
                                    track.getRequester().getAsMention()
                            )
                    );
            if (lastMessage == null)
            {
                lastTextChannel.sendMessageEmbeds(eb.build()).queue(message -> lastMessage = message);
            }
            else
            {
                lastMessage.editMessageEmbeds(eb.build()).queue(message -> lastMessage = message);
            }
        }
        catch (NullPointerException ignored) {}
    }

    @Override
    public String toString() {
        return String.format(
                "<TrackScheduler GuildChannel: %s, NowPlaying: %s, QueueSize= %d, autoDisconnectDone: %b",
                lastTextChannel,
                nowPlaying,
                queue.size(),
                autoDisconnectTimer.isCancelled() || autoDisconnectTimer.isDone()
        );
    }
}
