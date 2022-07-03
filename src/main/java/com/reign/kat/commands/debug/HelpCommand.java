package com.reign.kat.commands.debug;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.converters.Converter;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.utils.DiscordColor;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Collection;
import java.util.List;

public class HelpCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand()
    {
        super(new String[]{"help", "?"}, "Shows this message");
        addConverter(new StringConverter(
                "command",
                "command to get help with",
                true
        ));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        List<Category> categories = Bot.commandHandler.getCategories();
        String commandSearchTerm = params.get(0);
        log.info(commandSearchTerm);

        Command command = Bot.commandHandler.getCommand(commandSearchTerm);

        if (commandSearchTerm == null)
        {
            ctx.channel.sendMessageEmbeds(generateGenericHelp(ctx, categories).build()).queue();
        }
        else if (command != null)
        {
            ctx.channel.sendMessageEmbeds(generateSpecificHelp(ctx, command).build()).queue();
        }
        else
        {
            ctx.channel.sendMessageEmbeds(generateNoHelp(ctx, commandSearchTerm).build()).queue();
        }
    }

    EmbedBuilder generateNoHelp(Context ctx, String cmd)
    {
        return new EmbedBuilder()
                .setTitle("Kat Command Help")
                .setDescription(String.format("No Command Found\nCould not find a command starting with `%s`", cmd))
                .setFooter("kat.reign-network.co.uk")
                .setColor(DiscordColor.BACKGROUND_GREY);
    }

    EmbedBuilder generateSpecificHelp(Context ctx, Command command)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Kat Command Help")
                .setDescription(String.format("Information about `%s`", command.getPrimaryAlias()))
                .setFooter("kat.reign-network.co.uk")
                .setColor(DiscordColor.BACKGROUND_GREY);

        // Usage Help Section
        StringBuilder usageHelp = new StringBuilder().append("`").append(command.getPrimaryAlias()).append(" ");
        for(Converter<?> arg: command.converters)
        {
            if (arg.optional)
            {
                usageHelp.append("[").append(arg.argName).append("] ");
            }
            else
            {
                usageHelp.append("<").append(arg.argName).append("> ");
            }
        }
        usageHelp.append("`");

        // Command Description section
        String commandHelp = command.getDescription() +
                "\n\n" +
                "*For more information about commands* [**click here**](https://kat.reign-network.co.uk)";

        // Add em all
        embedBuilder.addField("Command Help", commandHelp, false)
                .addField("Usage", usageHelp.toString(), false);


        // Aliases Section (extra only if command has more than 1 alias
        if (command.getAliases().length > 1)
        {
            StringBuilder aliasesHelp = new StringBuilder();
            for(String alias: command.getAliases())
            {
                aliasesHelp.append("`").append(alias).append("`, ");
            }
            embedBuilder.addField("Aliases", aliasesHelp.substring(0, aliasesHelp.length()-2), false);
        }

        return embedBuilder;
    }
    EmbedBuilder generateGenericHelp(Context ctx, List<Category> categories)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Kat Commands")
                .setDescription("You can get more information about a command by doing `!devhelp [command]`")
                .setFooter("kat.reign-network.co.uk")
                .setColor(DiscordColor.BACKGROUND_GREY);

        for (Category cat: categories)
        {
            Collection<Command> commands = cat.getCommandsDistinct();

            // Avoid StringIndexOutOfBounds with categories that do not have commands.
            if (commands.size() > 0)
            {
                StringBuilder sb = new StringBuilder();
                log.info(String.valueOf(commands));
                for (Command cmd: commands)
                {
                    sb.append(String.format("`%s`, ", cmd.name()));
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

