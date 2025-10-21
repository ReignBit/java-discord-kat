package com.reign.kat.lib.voice.newvoice;

import com.reign.api.sponsorblock.SkipSegment;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record SponsorblockTrackMarkerHandler(RequestedTrack track, SkipSegment segment) implements TrackMarkerHandler {
    private static final Logger log = LoggerFactory.getLogger(SponsorblockTrackMarkerHandler.class);

    @Override
    public void handle(MarkerState state) {
        // We only care about reaching the marker.
        log.info("REACHED TRACKMARKER {}", state);
        if (state != MarkerState.REACHED) {
            return;
        }

        // For now we will be fairly relaxed about reaching skippable segments,
        // and only trigger the skip if we reach the exact start time. This will avoid
        // situations where the user has tried to seek into a segment, which they may want to do and not have it skip.

        log.info("Skipping non-music segment for video ID: {}", track.videoID);
        track.track.setPosition((long) (segment.stop * 1000));
    }
}
