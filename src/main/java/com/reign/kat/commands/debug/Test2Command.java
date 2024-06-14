package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.responses.ApiPlaylist;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Test2Command extends Command {
    private static final Logger log = LoggerFactory.getLogger(Test2Command.class);

    public Test2Command() {
        super(new String[]{"ap"}, "ap","Add trackto playlist.");
        addConverter(new StringConverter(
                "playlist",
                "playlist to add a track to.",
                null
        ));
        addConverter(new StringConverter(
                "url",
                "url to add",
                null
        ));
    }


    @Override
    public void execute(Context ctx, CommandParameters params)
    {
        ApiGuild g = ApiGuild.get(ctx.guild.getId());

        ApiPlaylist a = g.getPlaylist(params.get("playlist"));
        a.tracks.add(params.get("url"));
        log.info(a.toString());
        ctx.send(g.getPlaylists().toString());
    }

}

