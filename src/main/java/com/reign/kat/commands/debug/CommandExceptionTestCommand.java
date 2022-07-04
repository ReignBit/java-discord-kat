package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.StringConverter;

import com.reign.kat.lib.exceptions.InsufficientPermissionsCommandException;
import com.reign.kat.lib.exceptions.MissingArgumentCommandException;

public class CommandExceptionTestCommand extends Command {
    public CommandExceptionTestCommand() {
        super(new String[]{"except"}, "exceptest", "Test a range of CommandExceptions");
        addConverter(new StringConverter(
                "exception",
                "The name of the exception to trigger",
                null
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        String exception = args.get("exception");

        switch (exception) {
            case "InsufficientPermissions" ->
                    throw new InsufficientPermissionsCommandException("TEST Missing permissions for this command!");
            case "MissingArgument" -> throw new MissingArgumentCommandException("TEST Missing argument `test`");
            default -> throw new Exception("No exception found");
        }
    }
}
