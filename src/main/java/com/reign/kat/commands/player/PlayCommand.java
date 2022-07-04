package com.reign.kat.commands.player;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.IntConverter;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.exceptions.CommandException;

public class PlayCommand extends Command {

    public PlayCommand() {
        super(new String[]{"play","p"},"play" ,"Add song to queue");
        addConverter(new StringConverter(
                "test1",
                "this is test 1 (required)",
                null
        ));
        addConverter(new IntConverter(
                "test2",
                "this is test2 (optional)",
                1
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) {

    }

}
