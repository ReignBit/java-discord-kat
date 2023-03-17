package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.converters.IntConverter;
import com.reign.kat.lib.embeds.VoiceEmbed;

import com.reign.kat.lib.voice.newvoice.*;
import net.dv8tion.jda.api.EmbedBuilder;


import java.util.List;


public class QueueCommand extends Command {

    // TODO: Instead of this, maybe have dynamic limit based on the text limit of embeds? (4096)
    public static final int TRACK_DISPLAY_LIMIT = 30;

    public QueueCommand()
    {
        super(new String[]{"queue","q"},"queue" ,"Shows the playing queue");
        addConverter(new IntConverter(
                "offset",
                "Page of the queue to see.",
                1
        ));
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());
        int offset = args.get("offset");

        sendQueueEmbed(ctx, generateQueueString(playlist.nowPlaying(), playlist.getQueue(), offset));

    }

    private String generateQueueString(RequestedTrack nowPlaying, PlaylistQueue tracks, int offset)
    {
        if (offset > tracks.size()) { offset = 0; }
        if (offset < 0){ offset = 0; }

        int indexOffset = offset - 1;       // offset is user provided, meaning it's 1-indexed, we need it to be 0-indexed.
        StringBuilder sb = new StringBuilder();

        if (nowPlaying != null)
        {
            sb.append("**:musical_note: Now playing**  ").append(tracks.loopMode.emoji).append("\n");

            sb.append(buildTrackStringLine(-1, nowPlaying));
            sb.append("\n");
        }

        for (int i = indexOffset; i < Math.min(indexOffset + TRACK_DISPLAY_LIMIT, tracks.size()); i++) {
            sb.append(buildTrackStringLine(i+1, tracks.getQueue().get(i)));
        }
        if (sb.length() == 0)
        {
            sb.append("Nothing is queued!");
        }
        else
        {
            sb.append(
                    String.format(
                            "\n\n\nTotal tracks: **%d**. Showing tracks **%d**-**%d**.\nUse `%s` to see more tracks!",
                            tracks.size(),
                            offset,
                            indexOffset + Math.min(TRACK_DISPLAY_LIMIT, tracks.size()),
                            getSignature()
                    )
            );
        }

        return sb.toString();
    }

    private String buildTrackStringLine(int i, RequestedTrack track)
    {
        if (i < 0)
        {
            return String.format("%s [%s] : %s\n",
                    track.title,
                    track.getDurationAsTimestamp(),
                    track.requester.get().getAsMention()
            );
        }

        return String.format("%d. %s [%s] : %s\n",
                i,
                track.title,
                track.getDurationAsTimestamp(),
                track.requester.get().getAsMention()
        );
    }

    private void sendQueueEmbed(Context ctx, String str)
    {
        EmbedBuilder eb = new VoiceEmbed()
                .setTitle(String.format("%s's Queue", ctx.guild.getName()))
                .setDescription(str);

        ctx.send(eb.build());
    }
}
