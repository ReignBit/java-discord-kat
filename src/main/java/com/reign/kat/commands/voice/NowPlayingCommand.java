package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.NowPlayingEmbed;
import com.reign.kat.lib.voice.newvoice.RequestedTrack;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;

public class NowPlayingCommand extends Command
{
    public NowPlayingCommand()
    {
        super(new String[]{"nowplaying", "now", "np"}, "np", "Information about the current track");
        addPreCommand(GuildPlaylist::ensureTrackPlaying);
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        RequestedTrack track = playlist.nowPlaying();
        if (track != null)
        {
            ctx.send(new NowPlayingEmbed(track).setPausedNotification(playlist.isPaused()).build());
        }
    }
}