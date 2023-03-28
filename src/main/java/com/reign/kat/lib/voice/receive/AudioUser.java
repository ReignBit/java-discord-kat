package com.reign.kat.lib.voice.receive;

import kotlin.Suppress;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Recognizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AudioUser implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(AudioUser.class);
    private static final int AWAKE_GRACE_TIME_PASSES = 100;
    private static final int WAKE_DETECTION_GRACE_TIME_PASSES = 50;

    Recognizer recognizer;
    public Thread thread;

    AudioRecvManager manager;

    long guildID;
    long memberID;

    public String parsedSpeech = "";
    String debugWakeWordParsedSpeech = "";


    int gracePasses = AWAKE_GRACE_TIME_PASSES;  // 200ms grace before finishing the recognition
    int wakeGracePasses = WAKE_DETECTION_GRACE_TIME_PASSES; // 100ms grace for detecting wake word

    boolean awake = false;  // Are we processing an utterance, or just the wake word
    public ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);

    public AudioUser(long guild, User member, AudioRecvManager manager)
    {
        log.debug("New AudioUser");
        guildID = guild;
        memberID = member.getIdLong();
        this.manager = manager;

        try
        {
            recognizer = new Recognizer(VoiceRecognition.model, 16000);
        } catch (IOException e) { log.error("Failed to create recognizer", e); }

        log.debug("Starting thread...?");
        thread = new Thread(this,String.format("AudioUser-%d", memberID));
        thread.start();
    }

    public void write(byte[] data) throws IOException
    {
        buffer.write(data);
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run()
    {
        log.info("Started listening thread");
        while (manager.isListening)
        {

            byte[] transcoded = VoiceRecognition.transcode(buffer.toByteArray());
            buffer.reset();
            log.debug("awake: {}, parsed: {}, debugWWPS: {}", awake, parsedSpeech, debugWakeWordParsedSpeech);
            if (!awake)
            {
                processWakeWord(transcoded);
            }
            else
            {
                processAwake(transcoded);
            }

            try
            {
                Thread.sleep(20);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        log.debug("Exiting thread");
    }

    String stripResult(String result)
    {
        return result.split("\" : \"")[1].split("\"")[0];
    }

    void finish()
    {
        parsedSpeech = stripResult(recognizer.getFinalResult());
        log.debug("final="+parsedSpeech);

        manager.fireOnFinishedSpeakingEvent(memberID, this);
        recognizer.reset();
        parsedSpeech = "";
        gracePasses = AWAKE_GRACE_TIME_PASSES;
        awake = false;
    }

    void processWakeWord(byte[] transcoded)
    {
        if (!debugWakeWordParsedSpeech.equals(""))
            wakeGracePasses--;

        if (wakeGracePasses <= 0)
        {
            recognizer.reset();
            wakeGracePasses = WAKE_DETECTION_GRACE_TIME_PASSES;
        }

        recognizer.acceptWaveForm(transcoded, transcoded.length);
        debugWakeWordParsedSpeech = stripResult(recognizer.getPartialResult());
        if (VoiceRecognition.wakeWordUttered(stripResult(recognizer.getPartialResult())) != null)
        {
            awake = true;
            log.debug("Wake word uttered");
            recognizer.reset();
        }
    }

    void processAwake(byte[] transcoded)
    {
        log.debug("listening for command...");
        if (buffer.size() > 0)
            gracePasses = AWAKE_GRACE_TIME_PASSES;

        if (recognizer.acceptWaveForm(transcoded, transcoded.length) || (buffer.size() == 0 && gracePasses <= 0))
        {
            finish();
        }
        else
        {
            parsedSpeech = stripResult(recognizer.getPartialResult());

            if (!parsedSpeech.equals(""))
                gracePasses--;
            if (!Objects.equals(parsedSpeech, "")) { log.debug("{}     {}",parsedSpeech, gracePasses); }
        }
    }
}
