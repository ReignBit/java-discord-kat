package com.reign.kat.lib.command.category;


import com.reign.kat.lib.PermissionHandler;
import com.reign.kat.lib.command.ParentCommand;
import com.reign.kat.lib.exceptions.CommandException;
import com.reign.kat.lib.exceptions.InsufficientPermissionsCommandException;
import com.reign.kat.lib.utils.ExceptionMessageSender;
import com.reign.kat.lib.utils.PermissionGroupType;
import com.reign.kat.lib.utils.stats.BotStats;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public abstract class Category extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Category.class);

    public final String name = this.getClass().getCanonicalName();
    public final String shortName = this.getClass().getSimpleName();
    public String emoji = ":bricks:";

    private PermissionGroupType requiredPermission = PermissionGroupType.EVERYONE;
    private int requiredDiscordPermission = 0;

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
            log.trace("Registered command {} (alias {})", command.name, alias);
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

    public Command findCommand(String alias)
    {
        if(commands.containsKey(alias))
        {
            return commands.get(alias);
        }
        return null;
    }

    public void executeCommand(Context ctx)
    {
        log.trace("COMMAND {} started execution.", ctx.command.getPrimaryAlias());
        long then = Instant.now().toEpochMilli();

        CommandParameters params = new CommandParameters(ctx.event, String.join(" ", ctx.args).strip());
        /*
            In order to accommodate ParentCommands, we need to check if the 1st arg matches
            any subcommand alias in ParentCommand

            Then, we need to call the parent command, and its subcommand if execution of the parent command is valid

         */
        try
        {
            if (!isPrivileged(Objects.requireNonNull(ctx.event.getMember()), ctx.event.getTextChannel()))
            {
                throw new InsufficientPermissionsCommandException("You are not permitted to use this command!");
            }

            if (ctx.command instanceof ParentCommand parent)
            {
                parent.executeCommands(ctx, ctx.event, ctx.args);
            }
            else
            {
                params.parse(ctx.command);
                ctx.command.execute(ctx, params);



            }

            long l = Instant.now().toEpochMilli() - then;
            BotStats.addCommandExecutionStat(ctx.command, l, params);

        } catch (CommandException e)
        {
            ExceptionMessageSender.sendMessage(ctx, e);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            Arrays.stream(e.getStackTrace()).forEach(s -> log.error(String.valueOf(s)));
            ExceptionMessageSender.sendMessage(ctx, e);
        }
    }

    public void setRequiredPermissionGroup(PermissionGroupType permission)
    {
        requiredPermission = permission;
    }

    public void setRequiredDiscordPermissions(int permBitfield)
    {
        requiredDiscordPermission = permBitfield;
    }

    public boolean isPrivileged(Member member, GuildChannel channel)
    {
        log.trace("isPrivileged {}", Category.class.getCanonicalName());
        return PermissionHandler.isPrivileged(member, channel, requiredDiscordPermission, requiredPermission);
    }

    /**
     * Overridable, executed every hour since Bot start time.
     */
    public void onHourEvent()
    {
        // Does nothing by default
    }
}
