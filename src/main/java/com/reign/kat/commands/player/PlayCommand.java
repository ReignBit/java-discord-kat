package com.reign.kat.commands.player;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.IntConverter;
import com.reign.kat.lib.converters.MemberConverter;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.converters.UserConverter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Random;

public class PlayCommand extends Command {

    public PlayCommand() {
        super(new String[]{"play","p"},"play" ,"Add song to queue");
        addConverter(new StringConverter(
                "test1",
                "this is test 1 (required)",
                null
        ));
        addConverter(new IntConverter(
                "test2",
                "this is test2 (optional)",
                1
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) {

    }

}
