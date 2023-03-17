package com.reign.kat.lib.voice.newvoice;

import com.reign.kat.lib.snowflake.MemberId;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

import static com.reign.kat.lib.utils.Utilities.timeConversion;


public class RequestedTrack
{
    public final AudioTrack track;

    public final MemberId requester;
    public final long requestedTimestamp;

    public final long duration;
    public final String title;
    public final String author;
    public final String url;

    public RequestedTrack(Member member, AudioTrack track)
    {
        requester = MemberId.fromMember(member);
        this.track = track;

        requestedTimestamp = System.currentTimeMillis();
        duration = track.getDuration();

        title = track.getInfo().title;
        author = track.getInfo().author;
        url = track.getInfo().uri;
    }

    public RequestedTrack(MemberId member, AudioTrack track)
    {
        requester = member;
        this.track = track;

        requestedTimestamp = System.currentTimeMillis();
        duration = track.getDuration();

        title = track.getInfo().title;
        author = track.getInfo().author;
        url = track.getInfo().uri;
    }

    /**
     * Get the current progress through the track as a percenteage (0.0-1.0)
     * @return float percent played of the track.
     */
    public float getPercentComplete()
    {
        return (float)track.getPosition() / (float)track.getDuration();
    }

    public String getPositionAsTimestamp()
    {
        return timeConversion(track.getPosition());
    }

    public String getDurationAsTimestamp()
    {
        return timeConversion(track.getDuration());
    }

    public String toString() {
        return String.format("**[%s](%s)**\n[%s] Requested by: <@%s>", title, url, getDurationAsTimestamp(), requester.id);
    }

    /** Return a copy of the RequestedTrack instance. Use this when trying to play the same track twice */
    public RequestedTrack copy()
    {
        return new RequestedTrack(requester, track.makeClone());
    }
}
