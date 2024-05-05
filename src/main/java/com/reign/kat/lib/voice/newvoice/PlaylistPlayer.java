package com.reign.kat.lib.voice.newvoice;

import com.reign.kat.lib.voice.AudioPlayerSendHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlaylistPlayer extends AudioEventAdapter
{
    private static final Logger log = LoggerFactory.getLogger(PlaylistPlayer.class);

    public static final int ERROR_LIMIT = 3;

    /** LavaPlayer AudioPlayer instance. This class DOES NOT manage JDAs audio player. */
    public final AudioPlayer lavaPlayer;


    /** Current playing track - null if nothing is playing. */
    public RequestedTrack nowPlaying;


    public PlaylistPlayer(AudioPlayerManager manager, int volume)
    {

        lavaPlayer = manager.createPlayer();
        lavaPlayer.addListener(this);

        lavaPlayer.setVolume(volume);
    }

    /**
     * Immediately load and play the track.
     * @param track RequestedTrack to start playing.
     * @return boolean true if the play was successful.
     */
    public boolean play(RequestedTrack track)
    {
        nowPlaying = track;
        return lavaPlayer.startTrack(track.track, false);
    }

    /** Pause the player. */
    public void pause()
    {
        lavaPlayer.setPaused(true);
    }

    /** Resume the player. */
    public void resume()
    {
        lavaPlayer.setPaused(false);
    }

    /** Stops the current playing track, and do not play any next song in the queue. */
    public void stop()
    {

        lavaPlayer.stopTrack();
    }

    /**
     * Seek to position in the current playing track.
     * @param position long milliseconds to seek to.
     * @return long milliseconds position of the track.
     */
    public long seek(long position)
    {
        if(nowPlaying != null)
        {
            if (position <= nowPlaying.duration)
            {
                nowPlaying.track.setPosition(position);
            }
            return nowPlaying.track.getPosition();
        }
        return 0;
    }

    public AudioPlayerSendHandler getSendHandler()
    {
        return new AudioPlayerSendHandler(lavaPlayer);
    }

}
