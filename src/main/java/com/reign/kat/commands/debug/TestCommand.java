package com.reign.kat.commands.debug;

import com.reign.kat.core.command.Command;
import com.reign.kat.core.command.Context;

import java.time.Instant;

public class TestCommand extends Command {

    public TestCommand()
    {
        super(new String[]{"test"}, "I guess this would be the description?");
    }

    @Override
    public void execute(Context ctx) {
        ctx.channel.sendMessage(String.format("Pong!\n`Execution time: %dms`", Instant.now().toEpochMilli() - ctx.commandStartedAt)).queue();

    }
}
