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

public class RemoveCommand extends Command {

    public RemoveCommand()
    {
        super(new String[]{"remove"},"remove" ,"Remove a track from the queue");
        addConverter(new IntConverter(
                "from",
                "Remove from this position",
                null
        ));
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        int from = args.get("from");

        if (from > playlist.getQueue().size() || from < 0)
        {
            throw new IllegalArgumentException("You have entered an invalid position in the current queue!");
        }

        RequestedTrack pulledFromQueue = playlist.getQueue().getQueue().remove(from-1);

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Removed track!")
                .setDescription(
                        String.format("Removed %s", pulledFromQueue.title)
                );

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
