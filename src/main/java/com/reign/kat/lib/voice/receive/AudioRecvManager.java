package com.reign.kat.lib.voice.receive;

import com.reign.kat.Bot;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Member;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created per guild along with GuildPlaylist.
 * Contains the recognizers and handlers for each member in voice chat.
 */
public class AudioRecvManager extends ListenerAdapter implements AudioReceiveHandler
{
    private static final Logger log = LoggerFactory.getLogger(AudioRecvManager.class);

    private static final int USERS_IN_VOICE_THRESHOLD = 5;
    private static final int VOICE_TIMEOUT_SECONDS = 1;

    private final long guildID;

    private final Set<IAudioRecvListener> listeners = new HashSet<>();


    public boolean isListening = false;
    public Map<Long, AudioUser> users = new HashMap<>();



    public AudioRecvManager(GuildPlaylist parent)
    {
        this.guildID = parent.guildID;

        log.info("Created AudioRecvManager for guild {}", guildID);
        startListening();
    }


    /**
     * Add a listener to receive events
     * @param listener IAudioRecvListener
     */
    public void addListener(IAudioRecvListener listener)
    {
        listeners.add(listener);
    }

    void fireOnFinishedSpeakingEvent(long userID, AudioUser info)
    {
        Member member = Objects.requireNonNull(Bot.jda.getGuildById(guildID)).getMemberById(userID);
        listeners.forEach((l) -> l.onUserFinishedSpeaking(member, info));
    }

    /**
     * Starts listening for voice commands
     */
    public void startListening()
    {
        isListening = true;
        log.info("Started listening");
    }

    /**
     * Stops listening for voice commands
     */
    public void stopListening()
    {
        isListening = false;
        log.info("Stopped listening");
    }


    //// Events /////
    void userChannelUpdate(AudioChannel channel)
    {
        if (channel.getMembers().size() > USERS_IN_VOICE_THRESHOLD || channel.getMembers().size() == 0)
        {
            stopListening();
            log.info("Guild {} speech recog. disabled after reaching user threshold", channel.getGuild().getIdLong());
        }
        else if (!isListening)
        {
            startListening();
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

        // onGuildVoiceUpdate fires for both joining and leaving any channel in the guild.
        // This awful if clause is here to make sure it's our channel and the user has joined instead of left.
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

        // Respect user's privacy and don't listen if we're deafened.
        // because for some reason bots can still receive audio data when deafened...
        if(event.getMember().getIdLong() == Bot.jda.getSelfUser().getIdLong())
        {
            if (event.isGuildDeafened())
            {
                log.info("Guild {} was deafened!", event.getGuild().getIdLong());
                stopListening();
            }
            else
            {
                log.info("Guild {} was un-deafened!", event.getGuild().getIdLong());
                startListening();
            }
        }
    }

    //////// AudioReceiveHandler Overrides ///////
    @Override
    public boolean canReceiveCombined()
    {
        return isListening;
    }

    @Override
    public boolean canReceiveUser()
    {
        return isListening;
    }

    @Override
    public boolean canReceiveEncoded()
    {
        return false;
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio)
    {
        if (!isListening || userAudio.getUser().isBot()) { return; }
        long userID = userAudio.getUser().getIdLong();


        AudioUser info;
        if (users.containsKey(userID))
        {
            info = users.get(userID);
        }
        else
        {
            info = new AudioUser(guildID, userID);
            users.put(userID, info);
        }

        info.write(userAudio.getAudioData(1.0));
        users.putIfAbsent(userID, info);

    }
}
