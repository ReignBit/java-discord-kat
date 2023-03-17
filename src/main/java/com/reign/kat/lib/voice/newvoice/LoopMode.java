package com.reign.kat.lib.voice.newvoice;

public enum LoopMode
{
    NORMAL("\u25B6\uFE01"),
    ONCE("\uD83D\uDD01"),
    PLAYLIST("\uD83D\uDD02");

    public final String emoji;

    LoopMode(String emoji)
    {
        this.emoji = emoji;
    }
}
