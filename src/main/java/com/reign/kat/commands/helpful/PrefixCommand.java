package com.reign.kat.commands.helpful;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import com.reign.kat.lib.utils.DiscordColor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(HelpCommand.class);

    public PrefixCommand()
    {
        super(new String[]{"prefix"}, "prefix" ,"Change the command prefix.");
        addConverter(new StringConverter(
                "newPrefix",
                "New command prefix",
                ""
        ));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());
        String oldPrefix = guild.getPrefix();
        guild.prefix = params.get("newPrefix");

        if (guild.save())
        {
            EmbedBuilder eb = new GenericEmbedBuilder()
                    .setTitle("Changed Prefix")
                    .setDescription(String.format("Changed prefix from `%s` to `%s`", oldPrefix, guild.getPrefix()))
                    .setColor(DiscordColor.BACKGROUND_GREY);
            ctx.channel().sendMessageEmbeds(eb.build()).queue();
        }

    }
}
