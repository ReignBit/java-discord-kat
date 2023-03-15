package com.reign.kat.lib.voice.receive;

import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.LogLevel;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import org.vosk.Model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;


public class VoiceRecognition implements ConnectionListener
{
    private static final Logger log = LoggerFactory.getLogger(VoiceRecognition.class);
    Recognizer recognizer;

    public ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    public HashMap<String, ByteArrayOutputStream> userAudios = new HashMap<>();

    public VoiceRecognition()
    {
        LibVosk.setLogLevel(LogLevel.DEBUG);

        try
        {
            Model model = new Model("model");

            recognizer = new Recognizer(model, 16000);

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }


    public void addUserAudio(String id, byte[] audio)
    {

        if (!userAudios.containsKey(id))
        {
            userAudios.put(id, new ByteArrayOutputStream());
        }

        userAudios.get(id).write(audio, 0, audio.length);
//        log.debug("written {} data to userAudio {}", audio.length, id);
    }

    public String recognize(ByteArrayOutputStream stream)
    {

        if (recognizer.acceptWaveForm(stream.toByteArray(), stream.size())) {
             log.debug(recognizer.getResult());
            } else {
                log.debug(recognizer.getPartialResult());
            }
        stream.reset();
        return recognizer.getFinalResult();
    }

    @Override
    public void onPing(long ping)
    {

    }

    @Override
    public void onStatusChange(@NotNull ConnectionStatus status)
    {

    }

    @Override
    public void onUserSpeaking(@NotNull User user, boolean speaking)
    {
        log.debug("USER {} SPEAKING: {}", user.getName(), speaking);
//        if (userAudios.containsKey(user.getId()))
//        {
//            if (!speaking)
//            {
//                log.debug("Stopped speaking?");
//                recognize(userAudios.get(user.getId()));
//            }
//        }
//        else
//        {
//            userAudios.put(user.getId(), new ByteArrayOutputStream());
//        }
    }

    @Override
    public void onUserSpeaking(@NotNull User user, @NotNull EnumSet<SpeakingMode> modes)
    {
        ConnectionListener.super.onUserSpeaking(user, modes);
    }

    @Override
    public void onUserSpeaking(@NotNull User user, boolean speaking, boolean soundshare)
    {
        log.info("user: {} speaking: {}, soundshare: {}", user.getName(), speaking, soundshare);
        ConnectionListener.super.onUserSpeaking(user, speaking, soundshare);
    }
}