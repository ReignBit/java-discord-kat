package com.reign.kat.lib.voice.receive;

import com.reign.kat.Bot;
import com.reign.kat.lib.voice.GuildAudio;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;


public class AudioPlayerReceiveHandler implements AudioReceiveHandler
{
    private static final Logger log = LoggerFactory.getLogger(AudioPlayerReceiveHandler.class);
    private final VoiceRecognition voiceRecognition;

    public AudioPlayerReceiveHandler(GuildAudio manager)
    {
        this.voiceRecognition = manager.voiceRecognition;
    }

    @Override
    public boolean canReceiveCombined()
    {
        return false;
    }

    @Override
    public boolean canReceiveUser()
    {
        return true;
    }

    @Override
    public boolean canReceiveEncoded()
    {
        return false;
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio)
    {
//        log.debug(hex(userAudio.getAudioData(1.0)));
        if (!userAudio.getUser().getId().equals("172408031060033537"))
            return;

        voiceRecognition.addUserAudio(userAudio.getUser().getId(), userAudio.getAudioData(1.0));
    }

    public static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x ", aByte));
            // upper case
            // result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

}
