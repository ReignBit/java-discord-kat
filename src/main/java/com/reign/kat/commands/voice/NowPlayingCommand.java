package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.newvoice.RequestedTrack;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NowPlayingCommand extends Command
{
    private static final Logger log = LoggerFactory.getLogger(NowPlayingCommand.class);

    // Character to print for the current time on the playing progress bar
    String currentTimeSymbol = ":white_circle:";
    // Character to print for the rest of the playing progress bar
    String trackSymbol = "\u2550";

    // Total size of the playing progress bar
    int totalBarSize = 22;

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
            ctx.sendEmbeds(new VoiceEmbed().setTitle("Now Playing").setDescription(track + "\n" + buildProgressBar(track)).build());
        }


    }


    String buildProgressBar(RequestedTrack track)
    {
        int before = (int) (totalBarSize * track.getPercentComplete());
        return trackSymbol.repeat(before) + currentTimeSymbol + trackSymbol.repeat(totalBarSize - before);
    }
}