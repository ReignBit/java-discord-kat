package com.reign.kat.lib.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
    public void executeCommands(Context ctx, ContextEventAdapter event, ArrayList<String> args) throws Exception {
        if (!isPrivileged(Objects.requireNonNull(event.getMember()), event.getTextChannel()))
        {
            throw new IllegalStateException("You are not permitted to use this command!");
        }


        String subAlias = null;
        if (args.size() > 0)
        {
            subAlias = args.remove(0);
        }
        CommandParameters cmdParams = new CommandParameters(event, String.join("", args));

        execute(ctx, cmdParams);

        Command subcommand = getSubcommand(subAlias);
        if (subcommand != null)
        {
            // Subcommands do not need to check for permissions since they inherit the parent perms.
            cmdParams.parse(subcommand);
            subcommand.execute(ctx, cmdParams);
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
