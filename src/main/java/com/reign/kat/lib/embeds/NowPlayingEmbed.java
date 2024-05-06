package com.reign.kat.lib.embeds;

import com.reign.kat.lib.voice.newvoice.RequestedTrack;
import net.dv8tion.jda.api.entities.Member;

public class NowPlayingEmbed extends VoiceEmbed
{
    String CURRENT_TIME_SYMBOL = "";  // char for current time on progress bar
    String PROGRESS_BAR_SYMBOL = "\u2590";          // char to print for the rest of progress bar
    int TOTAL_BAR_SIZE = 25;                        // Total size of the playing progress bar

    public NowPlayingEmbed(RequestedTrack t)
    {
        Member m = t.requester.get();
        setDescription(buildProgressBar(t));
        setTitle(t.title, t.url);
        setFooter("Requested by: " + m.getEffectiveName(), m.getEffectiveAvatarUrl());
    }

    String buildProgressBar(RequestedTrack track)
    {
        int before = (int) (TOTAL_BAR_SIZE * track.getPercentComplete());

        return String.format("`%s%s%s`\t[%s/%s]",
                PROGRESS_BAR_SYMBOL.repeat(before),
                CURRENT_TIME_SYMBOL,
                "\u1CBC\u1CBC".repeat(TOTAL_BAR_SIZE - before),
                track.getPositionAsTimestamp(),
                track.getDurationAsTimestamp()
            );
    }
}
