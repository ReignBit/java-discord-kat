package com.reign.kat.commands;

import com.reign.kat.Bot;
import com.reign.kat.commands.lib.Command;
import com.reign.kat.commands.lib.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.time.Instant;

public class DebugCommand implements CommandExecutor {
    private static final Logger log = LogManager.getLogger(DebugCommand.class);


    @Command(aliases = {"ping"})
    public void pingCommand(Message message)
    {
        long timestamp = message.getCreationTimestamp().getNano();
        log.info(timestamp);
        long diff = Instant.now().getNano() - timestamp;
        message.getChannel().sendMessage(String.format("Pong!\nCommand completed in %.2fns", (float) diff / 1000000L));
    }

    @Command(aliases = {"status"})
    public void statusCommand(Message message, MessageAuthor author)
    {
        if (author.isBotOwner())
        {
            message.getChannel().sendMessage(buildStatusEmbed());

        }
    }

    EmbedBuilder buildStatusEmbed()
    {
        long now = Instant.now().getEpochSecond() - Bot.getStartedTimestamp();

        String timeString = "";
        if (now < 60L)
        {
            timeString = String.format("around `%d` seconds.", now);
        } else if (60 < now && now < 3600) {
            timeString = String.format("around `%d` minutes.", now);
        } else {
            timeString = String.format("around `%d` hours.", now);
        }

        return new EmbedBuilder()
                .setTitle("Java Kat Status Report")
                .setDescription(
                    "Java-Kat has been running for " + timeString
                ).addField("Kat Version", "0.0.1")
                .setTimestampToNow();
    }
}
