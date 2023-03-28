package com.reign.kat.lib.voice.receive;

import com.reign.kat.Bot;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import net.dv8tion.jda.api.entities.Member;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AudioRecvManager extends ListenerAdapter
{
    private static final Logger log = LoggerFactory.getLogger(AudioRecvManager.class);

    private static final int USERS_IN_VOICE_THRESHOLD = 3;
    private static final int VOICE_TIMEOUT_SECONDS = 1;


    private final long guildID;
    private final Set<IAudioRecvListener> listeners = new HashSet<>();

    private final ScheduledFuture<?> scheduledFuture;

    public final AudioRecvHandler handler;


    public AudioRecvManager(GuildPlaylist parent)
    {
        this.handler = new AudioRecvHandler();
        this.guildID = parent.guildID;

        scheduledFuture = Bot.executorService.scheduleAtFixedRate(loop(), 0, VOICE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        log.info("Created AudioRecvManager for guild {}", guildID);
        handler.startListening();
    }

    public boolean isEnabled() { return handler.isListening; }

    /**
     * Add a listener to receive events
     * @param listener IAudioRecvListener
     */
    public void addListener(IAudioRecvListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a listener from events
     * @param listener IAudioRecvListener
     */
    public void removeListener(IAudioRecvListener listener)
    {
        listeners.remove(listener);
    }

    public void destroy()
    {
        scheduledFuture.cancel(true);
    }

    private Runnable loop()
    {
        return () -> {
            long now = System.currentTimeMillis();
            handler.users.forEach((memberID, info) -> {

                if (info.lastSpoken > 0L)
                {
                    if(info.speaking && info.lastSize == info.buffer.size())
                    {
                        // User has finished speaking?
                        info.lastSpoken = 0L;
                        info.speaking = false;
                        fireOnFinishedSpeakingEvent(memberID, info);
                        info.buffer.reset();
                    }

                    info.lastSize = info.buffer.size();
                }
            });
        };
    }

    private void fireOnFinishedSpeakingEvent(long userID, AudioRecvHandler.AudioInfo info)
    {
        Member member = Objects.requireNonNull(Bot.jda.getGuildById(guildID)).getMemberById(userID);
        listeners.forEach((l) -> l.onUserFinishedSpeaking(member, info.buffer.toByteArray()));
    }

    void userChannelUpdate(AudioChannel channel)
    {
        if (channel.getMembers().size() > USERS_IN_VOICE_THRESHOLD || channel.getMembers().size() == 0)
        {
            handler.stopListening();
            log.info("Guild {} speech recog. disabled after reaching user threshold", channel.getGuild().getIdLong());
        }
        else if (!handler.isListening)
        {
            handler.startListening();
            log.info("Guild {} speech recog. started listening again.", channel.getGuild().getIdLong());
        }
    }
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event)
    {
        super.onGuildVoiceUpdate(event);
        if (!event.getVoiceState().inAudioChannel())
        {
            return;
        }
        assert event.getVoiceState().getChannel() != null;

        AudioChannel channel = event.getVoiceState().getChannel();

        if (
                (event.getChannelJoined() != null && event.getChannelJoined().getIdLong() == channel.getIdLong()) ||
                (event.getChannelLeft() != null && event.getChannelLeft().getIdLong() == channel.getIdLong())
        )
        {
            // Joined our channel
            userChannelUpdate(event.getVoiceState().getChannel());
        }
    }

    @Override
    public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent event)
    {
        super.onGuildVoiceGuildDeafen(event);
        log.info(event.getGuild().getId());

        if(event.getMember().getIdLong() == Bot.jda.getSelfUser().getIdLong())
        {
            if (event.isGuildDeafened())
            {
                log.info("Guild {} was deafened!", event.getGuild().getIdLong());
                handler.stopListening();
            }
            else
            {
                log.info("Guild {} was un-deafened!", event.getGuild().getIdLong());
                handler.startListening();
            }
        }

    }
}
