package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.JoinedChannelEmbed;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import com.reign.kat.lib.voice.newvoice.LoopMode;

public class LoopCommand extends Command
{
    public LoopCommand()
    {
        super(new String[]{"loop", "l"}, "loop", "Loop the playlist by track/playlist");
        addConverter(new StringConverter(
                "mode",
                "How to loop the playlist [off/track/playlist]",
                "off"
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        String title = " Looping off!";

        switch ((String) args.get("mode"))
        {
            case "off" ->
            {
                playlist.getQueue().loopMode = LoopMode.NORMAL;
                title = " Looping off!";
            }
            case "track", "once", "song" ->
            {
                playlist.getQueue().loopMode = LoopMode.ONCE;
                title = " Looping track!";
            }
            case "playlist" ->
            {
                playlist.getQueue().loopMode = LoopMode.PLAYLIST;
                title = " Looping playlist!";
            }
        }



        ctx.send(new VoiceEmbed().setTitle(playlist.getQueue().loopMode.emoji + title).build());
    }
}
