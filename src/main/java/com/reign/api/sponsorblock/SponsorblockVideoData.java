package com.reign.api.sponsorblock;

public class SponsorblockVideoData {
    public final SkipSegment[] segments;
    public final String videoID;

    public SponsorblockVideoData(String videoID, SkipSegment[] segments) {
        this.videoID = videoID;
        this.segments = segments;
    }
}
