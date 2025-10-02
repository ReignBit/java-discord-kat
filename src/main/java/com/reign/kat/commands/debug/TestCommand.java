package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.models.ApiGuildData;

import com.reign.kat.lib.command.Context;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.MessageContext;


public class TestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

    public TestCommand() {
        super(new String[]{"prefix"}, "prefix","Change the prefix of the server.");
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());
        Document test  = (Document) guild.getCommandData("tag");
        ctx.send(test.get("tags").toString());
    }

}

