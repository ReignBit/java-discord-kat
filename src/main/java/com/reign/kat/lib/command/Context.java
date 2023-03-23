package com.reign.kat.lib.command;

import com.reign.kat.Bot;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Context
{
    public final JDA jda = Bot.jda;

    /** List of command arguments */
    public final List<String> args;

    /** Invoked command */
    public final Command command;

    /** Member who invoked the command */
    public final Member author;

    /** Guild TextChannel the command was sent in. null if not in a guild */
    public final TextChannel channel;

    /** PrivateChannel the command was sent in. null if not in a DM */
    public final PrivateChannel dmChannel;

    /** The VoiceChannel the author is in. null if not in a voiceChannel / is a DM */
    public final AudioChannel voiceChannel;

    /** Guild the command was sent in. null if not in a guild */
    public final Guild guild;


    public abstract Event event();
    public abstract String prefix();
    public abstract void send(String... msgs);
    public abstract void send(MessageEmbed... embeds);

    public boolean canProvideInteractionHook() { return false; }
    public InteractionHook hook() { return null; }

    /**
     * Alias to send(String... msgs)
     * @param msgs String messages to send
     */
    public void reply(String... msgs) { send(msgs); }
    /**
     * Alias to send(MessageEmbed... embeds)
     * @param embeds MessageEmbed embeds to send
     */
    public void reply(MessageEmbed... embeds) { send(embeds); }


    /** Reply to the user with a generic error embed */
    public void sendError(String... msgs)
    {
        send(new ExceptionEmbed()
                .setTitle("An error occurred")
                .setDescription(String.join(" ",msgs))
                .build()
        );
    }

    /** Reply to the user with error an embed(s) */
    public void sendError(MessageEmbed... embeds)
    {
        send(embeds);
    }


    public MessageChannel channel() { return channel != null ? channel : dmChannel; }
    public boolean isGuild() { return guild != null; }
    public boolean isDM() { return dmChannel != null; }



    public Context(SlashCommandInteractionEvent event, Command command, List<String> args)
    {
        this.command = command;
        this.args = args;

        this.author = event.getMember();
        this.channel = event.getTextChannel();
        this.dmChannel = !event.isFromGuild() ? event.getPrivateChannel() : null;
        assert author != null;
        this.voiceChannel = getVoiceChannelFromMember(author);
        this.guild = event.isFromGuild() ? event.getGuild() : null;


    }

    public Context(MessageReceivedEvent event, Command command, List<String> args)
    {
        this.command = command;
        this.args = args;
        this.author = event.getMember();
        this.channel = event.getTextChannel();
        this.dmChannel = !event.isFromGuild() ? event.getPrivateChannel() : null;

        assert author != null;
        this.voiceChannel = getVoiceChannelFromMember(author);
        this.guild = event.isFromGuild() ? event.getGuild() : null;

    }

    private VoiceChannel getVoiceChannelFromMember(Member member)
    {
        if (member.getVoiceState() != null)
        {
            return (VoiceChannel) member.getVoiceState().getChannel();
        }
        return null;
    }

}
