package com.reign.kat.commands.debug;

import com.reign.kat.Bot;
import com.reign.kat.core.command.Command;
import com.reign.kat.core.command.Context;
import com.reign.kat.core.command.category.Category;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.awt.*;
import java.util.Collection;
import java.util.List;

public class HelpCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand()
    {
        super(new String[]{"help"}, "Shows this message");
    }

    @Override
    public void execute(Context ctx) {
        List<Category> categories = Bot.categoryHandler.getCategories();
        if (ctx.args.length < 2)
        {
            ctx.channel.sendMessageEmbeds(generateGenericHelp(ctx, categories).build()).queue();
            return;
        }

        //TODO: Add help per command

    }

    EmbedBuilder generateGenericHelp(Context ctx, List<Category> categories)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Kat Commands")
                .setDescription("You can get more information about a command by doing `!devhelp [command]`")
                .setFooter("kat.reign-network.co.uk")
                .setColor(Color.ORANGE);

        for (Category cat: categories)
        {
            Collection<Command> commands = cat.getCommands();

            // Avoid StringIndexOutOfBounds with categories that do not have commands.
            if (commands.size() > 0)
            {
                StringBuilder sb = new StringBuilder();
                for (Command cmd: commands)
                {
                    sb.append(String.format("`%s`, ", cmd.getPrimaryAlias()));
                }

                embedBuilder.addField(
                        String.format("%s %s",cat.emoji, cat.shortName.replaceFirst("Category", "")),
                        sb.substring(0, sb.length()-2),
                        false
                );
            }

        }

        return embedBuilder;
    }
}

