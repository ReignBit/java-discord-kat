package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.Bot;
import com.reign.kat.lib.converters.StringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;


public class TestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

    public TestCommand() {
        super(new String[]{"prefix"}, "prefix","Change the prefix of the server.");
        addConverter(new StringConverter("newPrefix", "The prefix to use", null));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        ApiGuild guild = Bot.api.getGuild(ctx.guild.getId());
        guild.prefix = params.get("newPrefix");

        Bot.api.updateGuild(guild);

        ApiGuild updatedGuild = Bot.api.getGuild(ctx.guild.getId());

        ctx.channel.sendMessage(updatedGuild.toString()).queue();
    }

}

