package com.reign.kat.lib.command.category;

import com.reign.kat.lib.utils.stats.BotStats;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.exceptions.MissingArgumentException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public abstract class Category extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Category.class);

    public final String name = this.getClass().getCanonicalName();
    public final String shortName = this.getClass().getSimpleName();
    public String emoji = ":bricks:";

    private final HashMap<String, Command> commands = new HashMap<>();

    /**
     * Get a Collection of all commands in the category.
     * @return Collection<Command>
     */
    public Command getCommand(String search)
    {
        if (commands.containsKey(search))
        {
            return commands.get(search);
        }
        return null;
    }

    public Collection<Command> getCommands()
    {
        return commands.values();
    }
    public Collection<Command> getCommandsDistinct()
    {
        return commands.values().stream().distinct().collect(Collectors.toList());
    }

    public void setEmoji(String emoji)
    {
        this.emoji = emoji;
    }

    /**
     * Register a command to listen for.
     * @param command Command to listen for.
     */
    public void registerCommand(Command command)
    {
        if (command.getPrimaryAlias() == null)
        {
            log.warn("Tried to register Command without any aliases. Ignoring command.");
            return;
        }
        for (String alias: command.getAliases())
        {
            if (commands.containsKey(alias))
            {
                Command orig = commands.get(alias);
                log.warn("Duplicate command alias registered. orig:{} new:{}", orig.name, command.name);
            }
            commands.put(alias, command);
            BotStats.addCommands(getCommandsDistinct());
            log.info("Registered command {} (alias {})", command.name, alias);
        }
    }

    /**
     * Unregister a command.
     * <br>Removes all aliases of the command from the register.
     * <br><br><i>Not sure why we'd want to do this, but is included for completeness.</i>
     * @param command Command to unregister.
     */
    public void unregisterCommand(Command command)
    {
        log.info("Attempting to unregister a command.");
        for (String alias: command.getAliases())
        {
            commands.remove(alias);
            log.info("Unregistered command {}", command.name);
        }
    }

    public Command findCommand(MessageReceivedEvent event, String alias)
    {
        if(commands.containsKey(alias))
        {
            return commands.get(alias);
        }
        return null;
    }

    public void executeCommand(MessageReceivedEvent event, Command command, ArrayList<String> args)
    {
        log.debug("COMMAND {} started execution.", command.getPrimaryAlias());
        long then = Instant.now().toEpochMilli();

        CommandParameters params = new CommandParameters(event);
        try
        {
            params.parse(args, command);
            command.execute(new Context(event, args), params);
        } catch (MissingArgumentException e)
        {
            event.getChannel().sendMessage(e.toString()).queue();
        }

        long l = Instant.now().toEpochMilli() - then;
        BotStats.addCommandExecutionStat(command, l, params);

    }
}
