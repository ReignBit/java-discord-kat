package com.reign.kat.lib.command.slash;

import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlashCommandContext extends Context
{
    private final SlashCommandInteractionEvent event;

    /** Information about the executing slash command */
    public final SlashCommandInteraction interaction;

    /** Webhook to the interaction, we can use this for
     * deferred replys up to 15 minutes after handling the interaction.
     */
    public final InteractionHook hook;

    public SlashCommandContext(SlashCommandInteractionEvent event, Command command, List<String> args)
    {
        super(event, command, args);
        this.event = event;

        this.interaction = event.getInteraction();
        this.hook = event.getHook();

        // TODO: Maybe we don't want to do this here?
        if (command.isShowTyping())
        {
            interaction.deferReply().queue();
        }
    }

    @Override
    public SlashCommandInteractionEvent event()
    {
        return event;
    }

    @Override
    public String prefix()
    {
        return "/";
    }

    @Override
    public boolean canProvideInteractionHook() { return true; }

    @Override
    public InteractionHook hook() { return hook; }

    @Override
    public void send(String... msgs)
    {
        if (!command.isShowTyping())
        {
            interaction.reply(String.join(" ", msgs)).queue();
        }
        else
        {
            hook.sendMessage(String.join(" ", msgs)).queue();
        }
    }

    @Override
    public void send(MessageEmbed... embeds)
    {
        if (!command.isShowTyping())
        {
            interaction.replyEmbeds(Arrays.asList(embeds)).queue();
        }
        else
        {
            hook.sendMessageEmbeds(Arrays.asList(embeds)).queue();
        }
    }

}
