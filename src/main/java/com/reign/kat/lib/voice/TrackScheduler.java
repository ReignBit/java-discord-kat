package com.reign.kat.lib.voice;

import com.reign.kat.Bot;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
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

    private final GuildAudioManager guildAudioManager;

    public TrackScheduler(AudioPlayer player, GuildAudioManager guildAudioManager)
    {
        this.player = player;
        this.queue = new ArrayList<>();
        this.guildAudioManager = guildAudioManager;
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
            log.info("Cancelled auto disconnect timer for : {}", guildAudioManager.guild.getId());

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
            player.startTrack(next.getTrack(), false);
            onSendNowPlayingMessage(nowPlaying);
            return;
        }
        nowPlaying = null;
        lastMessage = null;
        player.stopTrack();

        // Start an auto disconnect timer in case no tracks are queued in
        autoDisconnectTimer = Bot.executorService.schedule(this::onAutoDisconnect, Bot.properties.getVoiceAutoDisconnectMinutes(), TimeUnit.MINUTES);
        log.debug("started auto disconnect timer for: {}", guildAudioManager.guild.getId());
    }

    public ArrayList<RequestedTrack> getQueue()
    {
        return queue;
    }


    public void onAutoDisconnect()
    {
        guildAudioManager.autoDisconnect();
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
                "<TrackScheduler: GuildChannel: %s NowPlaying: %s QueueSize= %d",
                lastTextChannel,
                nowPlaying,
                queue.size()
        );
    }
}
