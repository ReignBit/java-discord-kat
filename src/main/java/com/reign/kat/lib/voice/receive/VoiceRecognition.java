package com.reign.kat.lib.voice.receive;

import com.reign.kat.Bot;
import com.reign.kat.lib.Config;
import com.reign.kat.lib.command.VoiceCommandEvent;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.LogLevel;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import org.vosk.Model;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/*
    TODO:
        - Figure a solution for hot-mics
        - Start/Stop listening commands
 */
public class VoiceRecognition implements IAudioRecvListener
{
    private static final Logger log = LoggerFactory.getLogger(VoiceRecognition.class);
    private static VoiceRecognition instance;
    private static Recognizer recognizer;

    private static final AtomicBoolean isLoaded = new AtomicBoolean(false);

    public static VoiceRecognition instance() { return instance; }

    public VoiceRecognition()
    {
        if (instance != null)
        {
            log.warn("Tried to create a VoiceRecognition instance when one already exists!");
            return;
        }
        instance = this;
    }

    public static void init()
    {
        new VoiceRecognition();
        LibVosk.setLogLevel(LogLevel.WARNINGS);
        try
        {
            long then = System.currentTimeMillis();
            log.info("Loading voice model. This may take a while...");
            Model model = new Model("voice-models/" + Config.SPEECH_RECOGNITION_MODEL_NAME);
            recognizer = new Recognizer(model, 16000);
            isLoaded.set(true);

            log.info("Voice model loaded in {}ms", System.currentTimeMillis() - then);
        } catch (IOException e)
        {
            log.error("Failed to initialize Recognizer.", e);
        }

    }

    public static boolean isRecognizerReady() { return isLoaded.get(); }

    @Override
    public void onUserFinishedSpeaking(Member member, byte[] data)
    {
        String speech = recognize(data).split("\" : \"")[1].split("\"")[0];

        if (speech.length() > 0)
        {
            log.debug("{} might have said: {}", member.getEffectiveName(), speech);

            String wakeWord = wakeWordUttered(speech);
            if (wakeWord != null)
            {
                //TODO: Fake a Command execution here.
                log.info("Wake word uttered for guild {}", member.getGuild().getIdLong());


                GuildPlaylist playlist = GuildPlaylistPool.get(member.getGuild().getIdLong());
                GuildChannel channel = Bot.jda.getTextChannelById(playlist.responseHandler.getTextChannelID());
                assert channel != null;
                log.info("last channel = {}", channel.getId());
                Bot.commandHandler.onVoiceCommandParsed(new VoiceCommandEvent(member.getGuild(), member, channel, speech, wakeWord));
            }
        }
    }


    private String wakeWordUttered(String speech)
    {
        for (String wakeWord :
                Config.SPEECH_RECOGNITION_WAKE_WORDS)
        {
            if (speech.startsWith(wakeWord))
            {
                return wakeWord;
            }
        }
        return null;
    }

    public static String recognize(byte[] stream)
    {
        if (isRecognizerReady())
        {
            byte[] transcodedAudio = transcode(stream);
            recognizer.acceptWaveForm(transcodedAudio, transcodedAudio.length);
            return recognizer.getFinalResult();
        }
        return "";
    }


    /**
     * Converts audio data from Discord's format (48Khz, 16-Bit Big-endian Stereo) to a format
     * that VOSK needs (16Khz 16-Bit Little-endian Mono).
     * @param origData audio PCM data to convert
     * @return 16Khz mono audio
     */
    private static byte[] transcode(byte[] origData)
    {
        AudioFormat original = new AudioFormat(48000.0f, 16, 2, true, true);
        AudioFormat target = new AudioFormat(16000.f, 16, 1, true,false);

        ByteArrayInputStream byteStream = new ByteArrayInputStream(origData);
        AudioInputStream originalStream = new AudioInputStream(byteStream, original, origData.length);
        try
        {
            return AudioSystem.getAudioInputStream(target, originalStream).readAllBytes();
        }
        catch (IOException e)
        {
            log.error("Failed to transcode audio stream.", e);
            return new byte[]{};
        }
    }
}