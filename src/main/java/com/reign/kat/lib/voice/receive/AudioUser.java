package com.reign.kat.lib.voice.receive;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Recognizer;

import java.io.*;

/**
 *
 */
public class AudioUser
{
/*    private static final Logger log = LoggerFactory.getLogger(AudioUser.class);
//    private static final int AWAKE_GRACE_TIME_PASSES = 100;
//    private static final int WAKE_DETECTION_GRACE_TIME_PASSES = 50;
//
//    Recognizer recognizer;
//    public Thread thread;
//
//    AudioRecvManager manager;
//
//    long guildID;
//    long memberID;
//
//    public String parsedSpeech = "";
//    String debugWakeWordParsedSpeech = "";
//
//
//    int gracePasses = AWAKE_GRACE_TIME_PASSES;  // 200ms grace before finishing the recognition
//    int wakeGracePasses = WAKE_DETECTION_GRACE_TIME_PASSES; // 100ms grace for detecting wake word
//
//    boolean awake = false;  // Are we processing an utterance, or just the wake word
//    public ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
//
//    public AudioUser(long guild, User member, AudioRecvManager manager)
//    {
//        log.debug("New AudioUser");
//        guildID = guild;
//        memberID = member.getIdLong();
//        this.manager = manager;
//
//        try
//        {
//            recognizer = new Recognizer(VoiceRecognition.model, 16000);
//        } catch (IOException e) { log.error("Failed to create recognizer", e); }
//
//        log.debug("Starting thread...?");
//        thread = new Thread(this,String.format("AudioUser-%d", memberID));
//        thread.start();
//    }
//
//    public void write(byte[] data) throws IOException
//    {
//        buffer.write(data);
//    }
//
//    @Override
//    @SuppressWarnings("BusyWait")
//    public void run()
//    {
//        log.info("Started listening thread");
//        while (manager.isListening)
//        {
//
//            byte[] transcoded = VoiceRecognition.transcode(buffer.toByteArray());
//            buffer.reset();
//
//            if (!awake)
//            {
//                processWakeWord(transcoded);
//            }
//            else
//            {
//                processAwake(transcoded);
//                log.debug("awake: {}, parsed: {}, debugWWPS: {}", awake, parsedSpeech, debugWakeWordParsedSpeech);
//            }
//
//            try
//            {
//                Thread.sleep(20);
//            } catch (InterruptedException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }
//
//        log.debug("Exiting thread");
//    }
//
//    String stripResult(String result)
//    {
//        return result.split("\" : \"")[1].split("\"")[0];
//    }
//
//    void finish()
//    {
//        parsedSpeech = stripResult(recognizer.getFinalResult());
//        log.debug("final="+parsedSpeech);
//
//        manager.fireOnFinishedSpeakingEvent(memberID, this);
//        recognizer.reset();
//        parsedSpeech = "";
//        gracePasses = AWAKE_GRACE_TIME_PASSES;
//        awake = false;
//    }
//
//    void processWakeWord(byte[] transcoded)
//    {
//        if (!debugWakeWordParsedSpeech.equals(""))
//            wakeGracePasses--;
//
//        if (wakeGracePasses <= 0)
//        {
//            recognizer.reset();
//            wakeGracePasses = WAKE_DETECTION_GRACE_TIME_PASSES;
//        }
//
//        recognizer.acceptWaveForm(transcoded, transcoded.length);
//        debugWakeWordParsedSpeech = stripResult(recognizer.getPartialResult());
//        if (VoiceRecognition.wakeWordUttered(stripResult(recognizer.getPartialResult())) != null)
//        {
//            awake = true;
//            log.debug("Wake word uttered");
//            recognizer.reset();
//        }
//    }
//
//    void processAwake(byte[] transcoded)
//    {
//        log.debug("listening for command...");
//        if (buffer.size() > 0)
//            gracePasses = AWAKE_GRACE_TIME_PASSES;
//
//        if (recognizer.acceptWaveForm(transcoded, transcoded.length) || (buffer.size() == 0 && gracePasses <= 0))
//        {
//            finish();
//        }
//        else
//        {
//            parsedSpeech = stripResult(recognizer.getPartialResult());
//
//            if (!parsedSpeech.equals(""))
//                gracePasses--;
//            if (!Objects.equals(parsedSpeech, "")) { log.debug("{}     {}",parsedSpeech, gracePasses); }
//        }
   } *
 */
    static final Logger log = LoggerFactory.getLogger(AudioUser.class);

    public final long guildID;
    public final long memberID;

    public final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Recognizer recognizer;
    boolean isAwake = false;            // Set to true when wake word has been spoken.

    public AudioUser(long guildID, long memberID)
    {
        this.guildID = guildID;
        this.memberID = memberID;

        log.debug("Created AudioUser for {}", memberID);
        setRecognizer();
    }
    public void write(byte[] data)
    {
        byte[] transcoded = VoiceRecognition.transcode(data);

        // Try to now recognize.
        isAwake = wakeWordCheck(transcoded);
        if (isAwake)
        {
            try
            {
                buffer.write(data);
            }
            catch( IOException ignored) { isAwake = false; }
        }

    }

    void setRecognizer()
    {
        try
        {
            recognizer = new Recognizer(VoiceRecognition.model, 16000);
        }
        catch (IOException e)
        {
            log.error("Failed to create recognizer {}", e.getMessage());
        }
    }

    boolean wakeWordCheck(byte[] audioSample)
    {
        String s;
        recognizer.acceptWaveForm(audioSample, audioSample.length);
        s = recognizer.getPartialResult().split(" : \"")[1].split("\"")[0];
        if (VoiceRecognition.wakeWordUttered(s) != null)
        {
            // Wake word said.
            log.info("Wake word said!");
            recognizer.reset();
            return true;
        }
        return false;
    }

}
