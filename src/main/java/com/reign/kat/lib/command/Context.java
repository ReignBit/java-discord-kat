package com.reign.kat.lib.command;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Context {
    public MessageReceivedEvent event;
    public JDA jda;

    public Message message;
    public Member author;
    public User authorAsUser;
    public List<MessageEmbed> embeds;
    public MessageEmbed embed;

    public TextChannel channel;
    public VoiceChannel voiceChannel;
    public Guild guild;
    public PrivateChannel privateChannel;

    @Deprecated
    public ArrayList<String> args;

    public boolean isGuild()
    {
        return guild != null;
    }
    public boolean isPrivateChannel()
    {
        return privateChannel != null;
    }

    public Context(MessageReceivedEvent event, ArrayList<String> args)
    {
        message = event.getMessage();
        author = event.getMember();
        authorAsUser = event.getAuthor();
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
        this.jda = Bot.jda;
    }
}
