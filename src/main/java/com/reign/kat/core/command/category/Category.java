package com.reign.kat.core.command.category;

import com.reign.kat.Bot;
import com.reign.kat.core.command.Command;
import com.reign.kat.core.command.Context;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;


public abstract class Category extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Category.class);

    public final String name = this.getClass().getCanonicalName();
    public final String shortName = this.getClass().getSimpleName();
    public String emoji = ":bricks:";

    private final HashMap<String, Command> commands = new HashMap<>();

    public Collection<Command> getCommands()
    {
        return commands.values();
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
            log.info("Registered command {}", command.name);
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

    public void executeCommand(MessageReceivedEvent event, Command command, String[] args)
    {
        log.info("COMMAND {} started execution.", command.getPrimaryAlias());
        long then = Instant.now().toEpochMilli();
        command.execute(new Context(event, args));
        log.info("COMMAND {} finished execution in {}ms", command.getPrimaryAlias(), Instant.now().toEpochMilli() - then);
    }
}
