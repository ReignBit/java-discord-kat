package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.BooleanConverter;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistTestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(PersistTestCommand.class);

    public PersistTestCommand() {
        super(new String[]{"pt"}, "pt","run persist test hook.");
        addConverter(new BooleanConverter("load", "should we load or save?", false));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        if (params.get("load")) {
            GuildPlaylistPool.loadPersistData();
        } else {
            GuildPlaylistPool.savePersistData();
        }
    }

}

