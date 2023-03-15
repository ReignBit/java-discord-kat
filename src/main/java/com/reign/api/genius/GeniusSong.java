package com.reign.api.genius;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public record GeniusSong(String url, String title, String artist, String lyrics)
{
    private static final Logger log = LoggerFactory.getLogger(GeniusSong.class);
    private static final int MAX_SEGMENT_LENGTH = 4000;

    @Override
    public String url()
    {
        return url;
    }

    @Override
    public String title()
    {
        return title;
    }

    @Override
    public String artist()
    {
        return artist;
    }

    @Override
    public String lyrics()
    {
        return lyrics;
    }

    public @NotNull List<String> lyricSegments()
    {
        long then = System.currentTimeMillis();
        LinkedList<String> segments = new LinkedList<>();

        int len = 0;

        StringBuilder sb = new StringBuilder();

        for (String seg :
                lyrics.split(" ") )
        {
            if (len + seg.length() > MAX_SEGMENT_LENGTH)
            {
                // new segment
                segments.add(sb.toString());
                len = 0;
            }
            else
            {
                sb.append(seg).append(" ");
                len += seg.length();
            }

        }

        if (segments.isEmpty())
        { // Lyrics are small enough to fit in one
            segments.add(lyrics);
        }

        log.info("Lyric segment compilation took: {}ms", System.currentTimeMillis() - then);
        return segments;
    }
}
