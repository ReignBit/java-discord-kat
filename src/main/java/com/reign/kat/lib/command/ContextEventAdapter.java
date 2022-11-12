package com.reign.kat.lib.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContextEventAdapter
{
    public Message message;
    public Member author;
    public User authorAsUser;
    public List<MessageEmbed> embeds;
    public MessageEmbed embed;

    public TextChannel channel;
    public VoiceChannel voiceChannel;
    public Guild guild;
    public PrivateChannel privateChannel;


    public MessageReceivedEvent messageReceivedEvent = null;
    public SlashCommandInteractionEvent slashCommandInteractionEvent = null;

    public ContextEventAdapter(SlashCommandInteractionEvent event)
    {
        guild = event.getGuild();
        message = null;
        author = event.getMember();
        authorAsUser = Objects.requireNonNull(event.getMember()).getUser();
        channel = event.getTextChannel();
        if (Objects.requireNonNull(event.getMember()).getVoiceState() != null)
        {
            voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
        } else {
            voiceChannel = null;
        }


        embeds = new ArrayList<>();

        slashCommandInteractionEvent = event;
    }
    public ContextEventAdapter(MessageReceivedEvent event)
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


        embeds = event.getMessage().getEmbeds();
        if (embeds.size() > 0) { embed = embeds.get(0); }

        messageReceivedEvent = event;
    }

    public Guild getGuild() { return guild; }
    public Message getMessage() { return message; }
    public User getAuthor() { return authorAsUser; }
    public Member getMember() { return author; }
    public TextChannel getTextChannel() { return channel; }
    public VoiceChannel getVoiceChannel() { return voiceChannel; }
    public List<MessageEmbed> getEmbeds() { return embeds; }
}
