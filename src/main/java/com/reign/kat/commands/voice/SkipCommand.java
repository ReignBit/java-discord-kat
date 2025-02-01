package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import net.dv8tion.jda.api.EmbedBuilder;


public class SkipCommand extends Command {

    public SkipCommand()
    {
        super(new String[]{"skip"},"skip" ,"Skips the playing song");
        addPreCommand(GuildPlaylist::ensureTrackPlaying);
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());
        playlist.skip();

        log.debug("SKIP::::: {}", playlist.nowPlaying());
    }
}
