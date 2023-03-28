package com.reign.kat.lib.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;

import java.util.Arrays;
import java.util.List;

public class VoiceContext extends Context
{
    String prefix;
    MessageChannel channel;

    public VoiceContext(VoiceCommandEvent event, Command command, List<String> args, String guildPrefix, GuildChannel channel)
    {
        super(event, command, args);

        prefix = guildPrefix;
        this.channel = (MessageChannel) channel;
    }

    @Override
    public Event event()
    {
        // This context is not created through JDA, there is no event
        return null;
    }

    @Override
    public String prefix()
    {
        return prefix;
    }

    @Override
    public void send(String... msgs)
    {
        channel.sendMessage(String.join(" ", msgs)).queue();
    }

    @Override
    public void send(MessageEmbed... embeds)
    {
        channel.sendMessageEmbeds(Arrays.asList(embeds)).queue();
    }
}
