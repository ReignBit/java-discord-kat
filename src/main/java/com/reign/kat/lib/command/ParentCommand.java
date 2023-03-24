package com.reign.kat.lib.command;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/* TODO: Why is this separate to Command? If we combine this with Command, we could then have sub-subcommands
    and not be limited to only have commands and sub-commands...
* */

/**
 * ParentCommand
 * A command which can have multiple subcommands attached to it.
 *
 * Acts like a regular command, with extra methods for children commands.
 * Add a new child command with `registerSubcommand(command);`, this can then be called
 * in Discord with `!command subcommand`.
 */
public abstract class ParentCommand extends Command {


    private final HashMap<String, Command> children = new HashMap<>();

    public ParentCommand(String[] aliases, String primaryAlias, String description) {
        super(aliases, primaryAlias, description);
    }

    public void registerSubcommand(Command command)
    {
        for(String alias: command.getAliases())
        {
            // Inherit parent permissions.
            command.requiredDiscordPermission = requiredDiscordPermission;
            command.requiredPermission = requiredPermission;
            children.put(alias, command);
        }
    }

    public Command getSubcommand(String alias)
    {
        if (children.containsKey(alias))
        {
            return children.get(alias);
        }
        return null;
    }

        /**
         * Calls the parent command before calling a subcommand
         * @param ctx Context of the command
         * @param args string args to convert into Converters
         */
    public void executeCommands(Context ctx, List<String> args) throws Exception {
        if (!isPrivileged(Objects.requireNonNull(ctx.author), (TextChannel) ctx.channel))
        {
            throw new IllegalStateException("You are not permitted to use this command!");
        }


        String subAlias = null;
        if (args.size() > 0)
        {
            subAlias = args.remove(0);
        }
        CommandParameters cmdParams = new CommandParameters(ctx, String.join("", args));

        invokeCommand(ctx, cmdParams);

        Command subcommand = getSubcommand(subAlias);
        if (subcommand != null)
        {
            // Subcommands do not need to check for permissions since they inherit the parent perms.
            cmdParams.parse(subcommand);
            subcommand.invokeCommand(ctx, cmdParams);
        }
    }

    @Override
    public String getSignature()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getPrimaryAlias()).append(" \n");
        for(Command cmd: children.values())
        {
            sb.append(getPrimaryAlias()).append(" ").append(cmd.getSignature()).append("\n");
        }
        return sb.delete(sb.length()-2, sb.length()).toString();
    }

    public abstract void execute(Context ctx, CommandParameters args) throws Exception;

}
