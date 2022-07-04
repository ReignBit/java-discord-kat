package com.reign.kat.lib.embeds;

import com.reign.kat.lib.utils.DiscordColor;
import net.dv8tion.jda.api.EmbedBuilder;

public class ExceptionEmbedBuilder extends GenericEmbedBuilder {
    public ExceptionEmbedBuilder(String emoji, String errorTitle, String errorMsg)
    {
        setTitle(String.format("%s %s", emoji, errorTitle))
                .setDescription(errorMsg)
                .setColor(DiscordColor.RED);
    }
}
