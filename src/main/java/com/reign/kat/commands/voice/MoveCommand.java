package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.IntConverter;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.newvoice.RequestedTrack;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import net.dv8tion.jda.api.EmbedBuilder;

public class MoveCommand extends Command {

    public MoveCommand()
    {
        super(new String[]{"move"},"move" ,"Move the track at `from` to position `to`");
        addConverter(new IntConverter(
                "from",
                "Move from this position",
                null
        ));

        addConverter(new IntConverter(
                "to",
                "Move to this position",
                null
        ));
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        int from = (Integer)args.get("from") - 1; // 1 = next in queue, so  pos 0
        int to = (Integer)args.get("to") - 1;

        if (from > playlist.getQueue().size() || to > playlist.getQueue().size())
        {
            throw new IllegalArgumentException("You have entered positions bigger than the current queue!");
        }
        if (from < 0 || to < 0)
        {
            throw new IllegalArgumentException("Positions entered must be bigger than 0");
        }

        RequestedTrack pulledFromQueue = playlist.getQueue().remove(from);
        playlist.getQueue().add(to, pulledFromQueue);

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Moved tracks!")
                .setDescription(
                        String.format("Moved %s to position %d", pulledFromQueue.title, to + 1)
                );

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
