package com.reign.kat.lib.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class ButtonInteractionContext extends Context
{

    private final ButtonInteractionEvent event;

    /** A list of embeds attached to the message */
    public final List<MessageEmbed> embeds;

    /** The first embed attached (if any) */
    public final MessageEmbed embed;

    /** The guild's customized prefix from ApiGuild*/
    public final String prefixGuild;

    /** The prefix used in the invoking of the Command*/
    public final String prefixUsed;

    /** The message which invoked the Command */
    public final Message message;



    public ButtonInteractionContext(ButtonInteractionEvent event, Command command, List<String> args, String prefixGuild, String prefixUsed )
    {
        super(event, command, args);
        this.event = event;

        message = event.getMessage();

        embeds = message.getEmbeds();
        embed = embeds.size() > 0 ? embeds.get(0) : null;

        this.prefixUsed = prefixUsed;
        this.prefixGuild = prefixGuild;
    }



    @Override
    public ButtonInteractionEvent event()
    {
        return event;
    }

    @Override
    public String prefix()
    {
        return prefixUsed;
    }

    @Override
    public void send(String... msgs)
    {
        message.reply(String.join(" ", msgs)).queue();
    }

    @Override
    public void send(MessageEmbed... embeds)
    {
        message.replyEmbeds(Arrays.asList(embeds)).queue();
    }



}
