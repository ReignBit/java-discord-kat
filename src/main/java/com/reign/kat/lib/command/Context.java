package com.reign.kat.lib.command;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Context {
    public ContextEventAdapter event;
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

    public String prefixGuild;
    public String prefixUsed;
    public Command command;

    public ArrayList<String> args;

    public boolean isGuild()
    {
        return guild != null;
    }
    public boolean isPrivateChannel()
    {
        return privateChannel != null;
    }
    public boolean isSlashInteraction() { return event.slashCommandInteractionEvent != null; }

    public void sendMessage(String msg)
    {
        if (isSlashInteraction())
        {
            event.slashCommandInteractionEvent.reply(msg).queue();
        }
        else
        {
            channel.sendMessage(msg).queue();
        }
    }

    public void sendEmbeds(MessageEmbed... embeds)
    {
        if (isSlashInteraction())
        {
            event.slashCommandInteractionEvent.replyEmbeds(Arrays.asList(embeds)).queue();
        }
        else
        {
            channel.sendMessageEmbeds(Arrays.asList(embeds)).queue();
        }
    }

    public Context(ContextEventAdapter event, ArrayList<String> args, String prefixGuild, String prefixUsed, Command command)
    {
        guild = event.getGuild();
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

        embeds = event.getEmbeds();
        this.args = args;
        this.event = event;
        this.jda = Bot.jda;

        this.prefixUsed = prefixUsed;
        this.prefixGuild = prefixGuild;
        this.command = command;
    }
}
