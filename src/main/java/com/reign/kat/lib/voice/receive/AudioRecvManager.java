package com.reign.kat.lib.voice.receive;

import com.reign.kat.Bot;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import net.dv8tion.jda.api.entities.Member;

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

    private static final int USERS_IN_VOICE_THRESHOLD = 0;
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

// TODO: This
//    @Override
//    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event)
//    {
//        super.onGuildVoiceUpdate(event);
//        if (event.getChannelJoined().asVoiceChannel() == )
//        {
//            if (getMembers().size() > USERS_IN_VOICE_THRESHOLD)
//            {
//                log.warn("AudioRecvPool for guild {} reached max user threashold. Disabling audio recv features.", guildID);
//                handler.stopListening();
//            }
//        }
//    }
//
//    @Override
//    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event)
//    {
//        super.onGuildVoiceLeave(event);
//
//        if (event.getChannelLeft().getMembers().size() <= USERS_IN_VOICE_THRESHOLD)
//        {
//            log.warn("AudioRecvPool for guild {} less than max users. Re-enabling audio recv.", guildID);
//            handler.startListening();
//        }
//
//        handler.users.remove(event.getMember().getIdLong());
//    }

    @Override
    public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent event)
    {
        super.onGuildVoiceGuildDeafen(event);
        log.debug(event.getMember().getId());
        if(event.getMember().getIdLong() == Bot.jda.getSelfUser().getIdLong())
        {
            if (event.isGuildDeafened())
            {
                handler.stopListening();
            }
            else
            {
                handler.startListening();
            }
        }

    }
}
