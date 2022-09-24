package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.IntConverter;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.GuildAudio;
import com.reign.kat.lib.voice.RequestedTrack;
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
        Integer from = args.get("from");
        Integer to = args.get("to");

        GuildAudio audioManager = VoiceCategory.guildAudio.getGuildManager(ctx.guild);
        if (from > audioManager.scheduler.getQueue().size() || to > audioManager.scheduler.getQueue().size())
        {
            throw new IllegalArgumentException("You have entered positions bigger than the current queue!");
        }
        if (from-1 < 1 || to-1 < 1)
        {
            throw new IllegalArgumentException("Positions entered must be bigger than 0");
        }

        RequestedTrack pulledFromQueue = audioManager.scheduler.getQueue().remove(from-1);
        audioManager.scheduler.getQueue().add(to-1, pulledFromQueue);

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Moved tracks!")
                .setDescription(
                        String.format("Moved %s to position %d", pulledFromQueue.getTrack().getInfo().title, to)
                );

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
