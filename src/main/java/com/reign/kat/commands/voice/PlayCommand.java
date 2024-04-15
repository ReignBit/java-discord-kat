package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;

import com.reign.kat.lib.converters.VideoSourceGreedyConverter;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(PlayCommand.class);
    public PlayCommand()
    {
        super(new String[]{"play","p"},"play" ,"Add song to queue");
        addConverter(new VideoSourceGreedyConverter(
                "search",
                "Name of a song or URL",
                ""
        ));
        setShowTyping(true);
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        playlist.getResponseHandler().setTextChannelID(ctx.channel().getIdLong());


        if (ctx.canProvideInteractionHook())
            playlist.getResponseHandler().setHook(ctx.hook());

        if (args.get("search").equals(""))
        {
            playlist.resume();
        }
        else
        {
            playlist.request(ctx.author, args.get("search"));
        }


    }
}
