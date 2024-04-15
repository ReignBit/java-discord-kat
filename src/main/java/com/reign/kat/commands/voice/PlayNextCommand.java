package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.VideoSourceGreedyConverter;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayNextCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(PlayNextCommand.class);
    public PlayNextCommand()
    {
        super(new String[]{"playnext","pn"},"playnext" ,"Add song to the start of the queue");
        addConverter(new VideoSourceGreedyConverter(
                "search",
                "Name of a song or URL",
                null
        ));
        setShowTyping(true);
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());


        playlist.request(ctx.author, args.get("search"));

    }
}
