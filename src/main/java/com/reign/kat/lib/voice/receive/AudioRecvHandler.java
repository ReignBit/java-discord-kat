package com.reign.kat.lib.voice.receive;

import com.reign.kat.lib.voice.GuildAudio;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


public class AudioRecvHandler implements AudioReceiveHandler
{
    public static class AudioInfo
    {
        public int lastSize = 0;
        public boolean speaking = false;
        public long lastSpoken = 0;
        public ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);

        public void write(byte[] data) throws IOException
        {
            lastSize = buffer.size();
            speaking = true;
            lastSpoken = System.currentTimeMillis();

            buffer.write(data);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AudioRecvHandler.class);


    public boolean isListening = false;

    public Map<Long, AudioInfo> users = new HashMap<>();


    public void startListening()
    {
        isListening = true;
        log.info("Started listening");
    }

    public void stopListening()
    {
        isListening = false;
        users.clear();

        log.info("Stopped listening");
    }

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

        long curTime = System.currentTimeMillis();
        long userID = userAudio.getUser().getIdLong();

        AudioInfo info = users.getOrDefault(userID, new AudioInfo());
        try
        {
            info.write(userAudio.getAudioData(1.0));
        } catch (IOException e)
        {
            log.warn("Failed to write audio data into buffer stream");
        }
        users.putIfAbsent(userID, info);

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
