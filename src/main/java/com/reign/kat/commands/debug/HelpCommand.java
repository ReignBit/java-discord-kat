package com.reign.kat.commands.debug;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.ParentCommand;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import com.reign.kat.lib.exceptions.CommandException;
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
        super(new String[]{"help", "?"}, "help" ,"Shows this message");
        addConverter(new StringConverter(
                "command",
                "command to get help with",
                ""
        ));
        addConverter(new StringConverter(
                "subcommand",
                "subcommand to get help with",
                ""
        ));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        List<Category> categories = Bot.commandHandler.getCategories();

        String commandSearchTerm = params.get("command");
        String subcommandSearchTerm = params.get("subcommand");

        Command command = null;
        Command subcommand = null;

        if(commandSearchTerm != null && !commandSearchTerm.trim().isEmpty())
        {
            command = Bot.commandHandler.getCommand(commandSearchTerm);
            if(subcommandSearchTerm != null && !subcommandSearchTerm.trim().isEmpty())
            {
                if (command instanceof ParentCommand parent)
                {
                    subcommand = parent.getSubcommand(subcommandSearchTerm);
                }
            }
        }


        if (subcommand != null)
        {
            sendEmbedsForCommand(subcommand, String.format("%s %s", commandSearchTerm, subcommandSearchTerm), categories, ctx);
        }
        else
        {
            sendEmbedsForCommand(command, commandSearchTerm, categories, ctx);
        }
    }

    public void sendEmbedsForCommand(Command command, String searchTerm, List<Category> categories, Context ctx)
    {
        if (searchTerm == null || searchTerm.trim().isEmpty())
        {
            ctx.channel.sendMessageEmbeds(generateGenericHelp(ctx, categories).build()).queue();
        }
        else if (command != null)
        {
            ctx.channel.sendMessageEmbeds(generateSpecificHelp(ctx, command).build()).queue();
        }
        else
        {
            ctx.channel.sendMessageEmbeds(generateNoHelp(ctx, searchTerm).build()).queue();
        }
    }

    EmbedBuilder generateNoHelp(Context ctx, String cmd)
    {
        return new GenericEmbedBuilder()
                .setTitle("Kat Command Help")
                .setDescription(String.format("No Command Found\nCould not find a command starting with `%s`", cmd))
                .setColor(DiscordColor.BACKGROUND_GREY);
    }

    EmbedBuilder generateSpecificHelp(Context ctx, Command command)
    {
        EmbedBuilder embedBuilder = new GenericEmbedBuilder()
                .setTitle("Kat Command Help")
                .setDescription(String.format("Information about `%s`", command.getPrimaryAlias()))
                .setColor(DiscordColor.BACKGROUND_GREY);

        // Usage Help Section
        StringBuilder usageHelp = new StringBuilder().append("`").append(command.getSignature()).append(" ");
        usageHelp.append("`").append("\n*[]: Optional, <>: Required*");

        // Command Description section
        String commandHelp = command.getDescription() +
                "\n\n" +
                "*For more information about commands* [**click here**](https://kat.reign-network.co.uk)";

        // Add em all
        embedBuilder.addField("Command Help", commandHelp, false)
                .addField("Usage", usageHelp.toString(), false);


        // Aliases Section (extra only if command has more than 1 alias
        if (command.getAliases().size() > 1)
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
        EmbedBuilder embedBuilder = new GenericEmbedBuilder()
                .setTitle("Kat Commands")
                .setDescription("You can get more information about a command by doing `!devhelp [command]`")
                .setColor(DiscordColor.BACKGROUND_GREY);

        for (Category cat: categories)
        {
            Collection<Command> commands = cat.getCommandsDistinct();

            // Avoid StringIndexOutOfBounds with categories that do not have commands.
            if (commands.size() > 0)
            {
                StringBuilder sb = new StringBuilder();
                for (Command cmd: commands)
                {
                    sb.append(String.format("`%s`, ", cmd.getName()));
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

