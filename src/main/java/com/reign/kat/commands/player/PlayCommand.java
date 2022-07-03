package com.reign.kat.commands.player;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.MemberConverter;
import com.reign.kat.lib.converters.UserConverter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.net.http.HttpResponse;
import java.util.Random;

public class PlayCommand extends Command {

    public PlayCommand() {
        super(new String[]{"play"}, "play song");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) {

    }

}
