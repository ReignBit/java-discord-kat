package com.reign.kat.lib.command.category;


import com.reign.kat.lib.PermissionHandler;
import com.reign.kat.lib.command.*;
import com.reign.kat.lib.exceptions.CommandException;
import com.reign.kat.lib.exceptions.InsufficientPermissionsCommandException;
import com.reign.kat.lib.utils.PermissionGroupType;
import com.reign.kat.lib.utils.PreCommandResult;
import com.reign.kat.lib.utils.stats.BotStats;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public abstract class Category extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Category.class);

    /** Internal name of the Category */
    public final String internalName = this.getClass().getCanonicalName();

    /** Name of the Category */
    public final String name = this.getClass().getSimpleName();

    /** Emoji shown in the help menus */
    public String helpMenuEmoji = ":bricks:";

    /** Required permission to execute the category's commands */
    private PermissionGroupType requiredPermission = PermissionGroupType.EVERYONE;

    /** Required permission in Discord to execute commands */
    private int requiredDiscordPermission = 0;

    /** Map of Commands to their aliases */
    private final HashMap<String, Command> commands = new HashMap<>();

    /** List of Pre commands to execute before any Commands */
    private final LinkedList<BiFunction<Context, CommandParameters, PreCommandResult>> precommands = new LinkedList<>();

    /**
     * Get a Collection of all commands in the category.
     * @return Collection<Command>
     */
    public Collection<Command> getCommands()
    {
        return commands.values();
    }

    /** Get a list of unique commands */
    public Collection<Command> getCommandsDistinct()
    {
        return commands.values().stream().distinct().collect(Collectors.toList());
    }

    /** Set the emoji to be shown in help menus */
    public void setHelpMenuEmoji(String helpMenuEmoji)
    {
        this.helpMenuEmoji = helpMenuEmoji;
    }


    /**
     * Add a Pre-command to all new commands registered
     * @param precommand A Function with return type boolean, `boolean myFunc(Context, CommandParameters)`
     */
    public void addPrecommand(BiFunction<Context, CommandParameters, PreCommandResult> precommand)
    {
        precommands.add(precommand);
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

        // precommands
        for (BiFunction<Context, CommandParameters, PreCommandResult> precommand :
                precommands)
        {
            command.addPreCommand(precommand);
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

        CommandParameters params = new CommandParameters(ctx, String.join(" ", ctx.args).strip());
        /*
            In order to accommodate ParentCommands, we need to check if the 1st arg matches
            any subcommand alias in ParentCommand

            Then, we need to call the parent command, and its subcommand if execution of the parent command is valid

         */
        try
        {
            if (!isPrivileged(Objects.requireNonNull(ctx.author), (TextChannel) ctx.channel))
            {
                throw new InsufficientPermissionsCommandException("You are not permitted to use this command!");
            }

            if (ctx.command instanceof ParentCommand parent)
            {
                parent.executeCommands(ctx, ctx.args);
            }
            else
            {
                params.parse(ctx.command);
                ctx.command.invokeCommand(ctx, params);
            }

            long l = Instant.now().toEpochMilli() - then;
            BotStats.addCommandExecutionStat(ctx.command, l, params);

        } catch (CommandException e)
        {
            ctx.sendError(e.getMessage());
        } catch (Exception e) {
            log.error(String.valueOf(e));
            log.error(Arrays.toString(e.getStackTrace()));

            ctx.sendError(e.getMessage());
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

    public boolean isPrivileged(Member member, TextChannel channel)
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
