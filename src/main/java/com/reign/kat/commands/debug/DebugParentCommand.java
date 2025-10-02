package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.ParentCommand;

public class DebugParentCommand extends Command
{
    public DebugParentCommand() {
        super(
                new String[] {"debug"},
                "debug",
                "A collection of debugging commands"
        );
        registerSubcommand(new TimingCommand());
        registerSubcommand(new KillCommand());
        registerSubcommand(new InvalidateCacheCommand());
        registerSubcommand(new CommandExceptionTestCommand());
        registerSubcommand(new TestCommand());
        registerSubcommand(new VoiceStateCommand());
        registerSubcommand(new DatastoreTestCommand());
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
    }
}
