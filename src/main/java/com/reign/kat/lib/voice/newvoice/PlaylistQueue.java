package com.reign.kat.lib.voice.newvoice;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FIFO queue for RequestedTracks, which also supports shuffling and moving elements.
 */
public class PlaylistQueue
{
    private static final Logger log = LoggerFactory.getLogger(PlaylistQueue.class);
    private final List<RequestedTrack> queue = new ArrayList<>();

    private final PlaylistPlayer player;

    public PlaylistTrackLoader loader = new PlaylistTrackLoader(this);
    public LoopMode loopMode = LoopMode.NORMAL;

    public PlaylistQueue(PlaylistPlayer player)
    {
        this.player = player;
    }

    /**
     * Add a RequestedTrack to the end of the queue.
     * @param track Track to add.
     */
    public void enqueue(RequestedTrack track)
    {
        queue.add(track);
    }

    public List<RequestedTrack> search(Member m, String q)
    {
        List<RequestedTrack> addedTracks = new ArrayList<>();

        for(AudioTrack t: loader.search(q))
        {
            RequestedTrack rt = new RequestedTrack(m,t);
            queue.add(rt);
            addedTracks.add(rt);
        }

        return addedTracks;
    }

    /**
     * Pops from the front of the queue.
     * @return RequestedTrack which was first in the queue.
     */
    public RequestedTrack dequeue()
    {
        log.debug("Looping mode is {}", loopMode);

        if (loopMode == LoopMode.NORMAL)
        {
            return queue.isEmpty() ? null : queue.remove(0);
        }
        else if (loopMode == LoopMode.ONCE)
        {
            return player.nowPlaying.copy();
        }
        else if (loopMode == LoopMode.PLAYLIST && !queue.isEmpty())
        {
            RequestedTrack t = queue.remove(0);
            queue.add(t.copy());
            return t;
        }

        return null;
    }

    /** Returns the next track without removing it from the queue */
    public RequestedTrack peek()
    {
        return queue.get(0);
    }

    public List<RequestedTrack> getQueue()
    {
        return queue;
    }

    /**
     * Shuffles the queue randomly.
     */
    public void shuffle()
    {
        Collections.shuffle(queue);
    }

    /**
     * Moves the track at index a to index b.
     * @param a Index to be moved.
     * @param b Index to move to.
     */
    public void move(int a, int b) throws IndexOutOfBoundsException
    {
        queue.add(b, queue.remove(a));
    }

    public int size() { return queue.size(); }

    /**
     * Clears the playlist.
     */
    public void clear()
    {
        queue.clear();
    }


}
