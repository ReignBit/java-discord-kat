package com.reign.kat.commands.fun.emote;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.MemberConverter;

import com.reign.kat.lib.exceptions.CommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;

public class SlapCommand extends Command {

    public SlapCommand() {
        super(new String[]{"slap"}, "slap", "Slap a user.");
        addConverter(new MemberConverter(
                "user",
                "The user you want to slap",
                null
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args){
        Member user = args.get(0);

        if (user != null)
        {
            ctx.channel.sendMessageEmbeds(slap(ctx.author.getEffectiveName(), user.getEffectiveName()).build()).queue();
        }
        else
        {
            ctx.channel.sendMessageEmbeds(slap(ctx.author.getEffectiveName(), "everyone").build()).queue();
        }
    }

    EmbedBuilder slap(String author, String name)
    {
        String gifUrl = Bot.tenorApi.get("/search", "slap%20anime").get().getRandomGif().url();

        return new EmbedBuilder()
                .setTitle(String.format("%s slaps %s!", author, name))
                .setImage(gifUrl)
                .setColor(Color.red);
    }
}
