package com.reign.kat.lib.utils;

import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExceptionMessageSender {
    private static final Logger log = LoggerFactory.getLogger("a");

    public static void sendMessage(MessageContext ctx, CommandException ce) {
        ctx.channel.sendMessageEmbeds(new ExceptionEmbedBuilder(ce.emoji, ce.title, ce.err).build()).queue();
    }

    public static void sendMessage(MessageContext ctx, Exception e) {
        ctx.channel.sendMessageEmbeds(new ExceptionEmbedBuilder(":x:", "An error has occurred", e.getMessage()).build()).queue();
    }
}

