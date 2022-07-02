package com.reign.kat.core.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Context {
    public Message message;
    public User author;
    public List<MessageEmbed> embeds;
    public MessageEmbed embed;

    public MessageReceivedEvent event;

    public TextChannel channel;
    public VoiceChannel voiceChannel;
    public Guild guild;
    public PrivateChannel privateChannel;

    public long commandStartedAt = Instant.now().toEpochMilli();

    public String[] args;

    public boolean isGuild()
    {
        return guild != null;
    }

    public boolean isPrivateChannel()
    {
        return privateChannel != null;
    }

    public Context(MessageReceivedEvent event, String[] args)
    {
        message = event.getMessage();
        author = event.getAuthor();
        channel = event.getTextChannel();
        if (Objects.requireNonNull(event.getMember()).getVoiceState() != null)
        {
            voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
        } else {
          voiceChannel = null;
        }


        embeds = event.getMessage().getEmbeds();
        if (embeds.size() > 0) { embed = embeds.get(0); }

        this.args = args;
        this.event = event;
    }
}
