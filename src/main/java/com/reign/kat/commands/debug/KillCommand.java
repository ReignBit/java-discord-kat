package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KillCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(KillCommand.class);

    public KillCommand() {
        super(new String[]{"kill"}, "kill" ,"Stops the bot");
    }


    // !help @user
    @Override
    public void execute(Context ctx, CommandParameters params) {

    }
}
