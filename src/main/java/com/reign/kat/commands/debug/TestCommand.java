package com.reign.kat.commands.debug;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class TestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

    public TestCommand() {
        super(new String[]{"test"}, "test","I guess this would be the description?");
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        ArrayList<Long> ids = Bot.api.getGuildIds().ids();

        EmbedBuilder apiGuildsEmbedBuilder = new EmbedBuilder();
        apiGuildsEmbedBuilder.setTitle("Guilds which have API data")
                        .setDescription(getGuildDataFromIds(ids));

        EmbedBuilder nonApiEmbedBuilder = new EmbedBuilder();
        nonApiEmbedBuilder.setTitle("Guilds which are not in API")
                        .setDescription(getMissingGuildsFromIds(ids));


        ctx.channel.sendMessageEmbeds(
                apiGuildsEmbedBuilder.build(),
                nonApiEmbedBuilder.build()

        ).queue();
    }

    String getGuildDataFromIds(ArrayList<Long> ids)
    {
        StringBuilder sb = new StringBuilder().append("```\n");
        for(long id: ids)
        {
            Guild g = Bot.jda.getGuildById(id);
            if (g != null)
            {
                sb.append(g.getName()).append("\t").append(g.getMemberCount()).append(" Users\n");
            }
            else
            {
                sb.append(id).append("\tNotAvailable\n");
            }
        }
        sb.append("```");
        return sb.toString();
    }

    String getMissingGuildsFromIds(ArrayList<Long> ids)
    {
        StringBuilder sb = new StringBuilder().append("```\n");


        for(Guild g: Bot.jda.getGuilds())
        {
            if (!ids.contains(g.getIdLong()))
            {
                sb.append(g.getName()).append("   ").append(g.getMemberCount()).append(" Users\n");
            }
        }
        sb.append("```");
        return sb.toString();
    }

}

