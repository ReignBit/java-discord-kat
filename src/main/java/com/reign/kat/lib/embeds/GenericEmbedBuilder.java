package com.reign.kat.lib.embeds;

import com.reign.kat.Bot;
import com.reign.kat.BotVersion;
import net.dv8tion.jda.api.EmbedBuilder;

public class GenericEmbedBuilder extends EmbedBuilder{

    /**
     * Generic embed with footer set to show the website and current version number of the bot.
     */
    public GenericEmbedBuilder()
    {
        //setFooter("kat.reign-network.co.uk \u2022 v" + BotVersion.version() );
    }
}
