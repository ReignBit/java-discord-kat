package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.models.ApiGuildData;
import com.reign.kat.Bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;


public class TestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

    public TestCommand() {
        super(new String[]{"prefix"}, "prefix","Change the prefix of the server.");
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        ApiGuildData guild = ApiGuildData.get(ctx.guild.getId());


        ctx.channel.sendMessage(String.format("**%s** Before changes\n```\n%s\n```", ctx.guild.getId(), guild.toString())).queue();
        guild.level.enabled = !guild.level.enabled;

        ctx.channel.sendMessage(String.format("**%s** Before commit\n```\n%s\n```", ctx.guild.getId(), guild)).queue();
        guild.save();

        ctx.channel.sendMessage(String.format("**%s** after changes\n```\n%s\n```", ctx.guild.getId(), guild)).queue();
    }

}

