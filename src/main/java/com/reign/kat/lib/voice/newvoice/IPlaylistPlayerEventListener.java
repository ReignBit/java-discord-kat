package com.reign.kat.lib.voice.newvoice;

public interface IPlaylistPlayerEventListener
{
    void onDisconnect(String reason);
    void onNowPlaying(RequestedTrack track);

}
