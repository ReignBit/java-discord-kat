package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.ParentCommand;

public class DebugParentCommand extends ParentCommand {
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
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
    }
}
