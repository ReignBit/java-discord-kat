package com.reign.api.sponsorblock;

public class SkipSegment {
    public float start, stop;

    public SkipSegment(String videoID, float start, float stop) {
        this.start = start;
        this.stop = stop;
    }
}
