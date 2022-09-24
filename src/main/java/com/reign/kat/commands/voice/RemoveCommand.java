package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.IntConverter;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.GuildAudio;
import com.reign.kat.lib.voice.RequestedTrack;
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
        Integer from = args.get("from");

        GuildAudio audioManager = VoiceCategory.guildAudio.getGuildManager(ctx.guild);
        if (from > audioManager.scheduler.getQueue().size() || from < 0)
        {
            throw new IllegalArgumentException("You have entered an invalid position in the current queue!");
        }

        RequestedTrack pulledFromQueue = audioManager.scheduler.getQueue().remove(from-1);

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Removed track!")
                .setDescription(
                        String.format("Removed %s", pulledFromQueue.getTrack().getInfo().title)
                );

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
