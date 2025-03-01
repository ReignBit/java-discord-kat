package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.CommandParameters;

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
        registerSubcommand(new CommandExceptionTestCommand());
        registerSubcommand(new TestCommand());
        registerSubcommand(new VoiceStateCommand());
        registerSubcommand(new PersistTestCommand());
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
    }
}
