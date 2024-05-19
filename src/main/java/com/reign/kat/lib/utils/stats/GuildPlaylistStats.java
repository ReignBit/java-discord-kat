package com.reign.kat.lib.utils.stats;

import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuildPlaylistStats
{
    public record GuildStats(long id, boolean isPlaying, int queueSize) {}
    public record GuildPlaylistReport(int totalPlaying, List<GuildStats> stats) {
    }

    public static GuildPlaylistReport get()
    {
        int playing = 0;

        List<GuildStats> guildStats = new ArrayList<>();

        for (Iterator<Map.Entry<Long, GuildPlaylist>> it = GuildPlaylistPool.all(); it.hasNext(); )
        {
            Map.Entry<Long, GuildPlaylist> entry = it.next();
            long id = entry.getKey();
            GuildPlaylist gp = entry.getValue();

            if (gp.isPlaying()) { playing++; }
            int queueSize = gp.getQueue().size();

            guildStats.add(new GuildStats(id, gp.isPlaying(), queueSize));

        }

        return new GuildPlaylistReport(playing, guildStats);
    }

}
