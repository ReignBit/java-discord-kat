package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.VideoSourceGreedyConverter;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PauseCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(PauseCommand.class);
    public PauseCommand()
    {
        super(new String[]{"pause"},"pause" ,"Pause the current song playing");
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());
        playlist.getResponseHandler().setTextChannelID(ctx.channel().getIdLong());


        if (ctx.canProvideInteractionHook())
            playlist.getResponseHandler().setHook(ctx.hook());

        if (playlist.isPlaying())
        {
            playlist.pause();
        } else {
            ctx.send(new VoiceEmbed()
                    .setTitle("Nothing is playing!")
                    .build()
            );
        }


    }
}
