package com.reign.kat.commands.voice;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.VideoSourceGreedyConverter;
import com.reign.kat.lib.voice.music.TrackResultHandler;
import dev.arbjerg.lavalink.client.Link;

public class PlayCommand extends Command
{
    public PlayCommand()
    {
        super(new String[]{"play", "p"}, "play", "Play/search for a song");
        addConverter(new VideoSourceGreedyConverter(
                "searchQuery",
                "Track name to play",
                null
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        final long guildId = ctx.guild.getIdLong();
        final Link link = Bot.lavalink.getOrCreateLink(guildId);
        final long channelId = ctx.channel.getIdLong();
        link.loadItem(args.get("searchQuery"))
                .subscribe(new TrackResultHandler(
                        channelId,
                        ctx.author,
                        args.get("searchQuery"),
                        link
                ));
    }
}
