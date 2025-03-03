package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InvalidateCacheCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(InvalidateCacheCommand.class);

    public InvalidateCacheCommand() {
        super(new String[]{"invalidate"}, "invalidate" ,"Invalidate caches");
    }


    // !help @user
    @Override
    public void execute(Context ctx, CommandParameters params) {
        ApiGuild.invalidateCache();
        ctx.send("Invalidated ApiGuild cache!");
    }
}
