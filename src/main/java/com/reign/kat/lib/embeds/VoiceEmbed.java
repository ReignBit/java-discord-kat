package com.reign.kat.lib.embeds;

import com.reign.kat.lib.utils.KatColor;

public class VoiceEmbed extends GenericEmbedBuilder {

    public VoiceEmbed()
    {
        setColor(KatColor.VOICE_CATEGORY);
    }

    public VoiceEmbed setPausedNotification(boolean set)
    {
        if (set)
            setFooter("Player is paused - Use `play` to unpause.");

        return this;
    }
}
