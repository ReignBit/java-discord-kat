package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.utils.Utilities;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SeekCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(SeekCommand.class);
    public SeekCommand()
    {
        super(new String[]{"seek"},"seek" ,"Seek through the current track");
        addConverter(new StringConverter(
                "position",
                "Position in the track to seek to. Formatted as a timestamp (eg: 15:24) (+/- relative)",
                "0"
        ));

        addPreCommand(GuildPlaylist::ensureTrackPlaying);
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        String position = args.get("position");

        long requestedPosition;
        if (position.startsWith("+") || position.startsWith("-")) {
            // Relative seeking

            boolean subtract = position.startsWith("-");

            long currentPositionMs = playlist.nowPlaying().track.getPosition();
            requestedPosition = Utilities.stringToTimeConversion(position.substring(1));

            if (subtract)
                requestedPosition = -requestedPosition;

            requestedPosition = currentPositionMs + requestedPosition;
        } else {
            requestedPosition = Utilities.stringToTimeConversion(args.get("position"));
        }

        if (requestedPosition < 0L)
        {
            // Failed to convert to a timestamp
            ctx.send(new ExceptionEmbed()
                    .setTitle("Invalid time to seek to")
                    .setDescription(String.format("`{}` is an invalid timestamp!",args.get("position"))).build());
            return;
        }

        if (requestedPosition > playlist.nowPlaying().duration)
        {
            requestedPosition = playlist.nowPlaying().duration;
        }
        playlist.seek(requestedPosition);

        ctx.send(new VoiceEmbed()
                .setTitle("Seeked track")
                .setDescription("Set track position to " + Utilities.timeConversion(requestedPosition)).build());
    }
}
