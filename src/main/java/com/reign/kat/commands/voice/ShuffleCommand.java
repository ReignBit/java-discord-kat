package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.GuildAudio;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collections;


public class ShuffleCommand extends Command {

    public ShuffleCommand()
    {
        super(new String[]{"shuffle"},"shuffle" ,"Shuffles the queue");
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        Guild guild = ctx.guild;
        GuildAudio guildAudio = VoiceCategory.guildAudio.getGuildManager(guild);


        Collections.shuffle(guildAudio.scheduler.queue);
        sendShuffleEmbed(ctx);

    }


    private void sendShuffleEmbed(Context ctx)
    {
        EmbedBuilder eb = new VoiceEmbed()
                .setTitle(String.format("%s's Queue", ctx.guild.getName()))
                .setDescription(":game_die: Shuffled the queue!");

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
