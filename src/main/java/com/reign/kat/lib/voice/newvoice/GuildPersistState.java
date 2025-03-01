package com.reign.kat.lib.voice.newvoice;

public class GuildPersistState {
    public final long guildId;
    public final long voiceChannelId;
    public final long textChannelId;
    public final String nowPlayingUrl;
    public final long nowPlayingTimestamp;

    public final String[] tracks;

    public GuildPersistState(long guildId, long vcId, long tcId, String npStr, long npTime, String[] tracks)
    {
        this.guildId = guildId;
        this.voiceChannelId = vcId;
        this.textChannelId = tcId;
        this.nowPlayingUrl = npStr;
        this.nowPlayingTimestamp = npTime;
        this.tracks = tracks;
    }
}
