package com.reign.kat.lib.voice.newvoice;

import com.reign.api.sponsorblock.SkipSegment;
import com.reign.api.sponsorblock.SponsorblockAPI;
import com.reign.kat.lib.snowflake.MemberId;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.reign.kat.lib.utils.Utilities.timeConversion;


public class RequestedTrack
{
    private static final Logger log = LoggerFactory.getLogger(RequestedTrack.class);
    public final AudioTrack track;

    public final MemberId requester;
    public final long requestedTimestamp;

    public final long duration;
    public final String title;
    public final String author;
    public final String url;
    public final String videoID; // Only for YouTube
    public final boolean isYoutube;

    public RequestedTrack(Member member, AudioTrack track)
    {
        requester = MemberId.fromMember(member);
        this.track = track;

        requestedTimestamp = System.currentTimeMillis();
        duration = track.getDuration();

        title = track.getInfo().title;
        author = track.getInfo().author;
        url = track.getInfo().uri;

        /* Sponsorblock video ID */
        isYoutube = url.toLowerCase().contains("youtube") || url.toLowerCase().contains("youtu.be");
        if (isYoutube) {
            videoID = url.split("\\?v=")[1].split("&")[0];
            loadVideoSkipSegments();
        } else {
            videoID = null;
        }
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

        /* Sponsorblock video ID */
        isYoutube = url.toLowerCase().contains("youtube") || url.toLowerCase().contains("youtu.be");
        if (isYoutube) {
            videoID = url.split("v\\?=")[1].split("&")[0];
            loadVideoSkipSegments();
        } else {
            videoID = null;
        }
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


    private void loadVideoSkipSegments() {
        SkipSegment[] skipSegments = SponsorblockAPI.getSkipSegments(videoID);
        if (skipSegments == null) { return; }

        for (SkipSegment ss : skipSegments) {
            track.addMarker(new TrackMarker(
                    (long)(ss.start * 1000) + 20,   // Add a 20ms delay so we are guaranteed to hit when pos = 0
                    new SponsorblockTrackMarkerHandler(this, ss)
            ));
            log.debug("Added skipsegment: {}, {} -> {}", videoID, (long)(ss.start * 1000), ss.stop);
        }
    }
}
