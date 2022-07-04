package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.ParentCommand;
import com.reign.kat.lib.exceptions.InsufficientPermissionsCommandException;

public class DebugParentCommand extends ParentCommand {
    public DebugParentCommand() {
        super(
                new String[] {"debug"},
                "debug",
                "A collection of debugging commands"
        );
        addSubcommand(new TimingCommand());
        addSubcommand(new CommandExceptionTestCommand());
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
    }
}
