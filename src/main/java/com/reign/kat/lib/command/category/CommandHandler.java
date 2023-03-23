package com.reign.kat.lib.command.category;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;

import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.command.slash.SlashCommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * CommandCategories
 *
 * Contains all categories used by the Bot.
 */
public class CommandHandler extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private static final ArrayList<Category> categories = new ArrayList<>();

    private static final HashMap<String, Category> cmdCatMap = new HashMap<>();

    public void addCategory(Category cat)
    {
        if (categories.contains(cat))
        {
            log.warn("Already registered Category {}", cat);
        }
        categories.add(cat);




        // Populate the Command Category map for ease of finding commands.
        for (Command cmd: cat.getCommandsDistinct()) {
            HashSet<String> aliases = cmd.getAliases();
            for (String a: aliases) {
                cmdCatMap.put(a, cat);
            }
        }

//
    }

    public Category removeCategory(String name)
    {
        for (Category cat: categories)
        {
            if (cat.internalName.equalsIgnoreCase(name))
            {
                categories.remove(cat);
                for (Command cmd: cat.getCommands()) {
                    HashSet<String> aliases = cmd.getAliases();
                    for (String a: aliases) {
                        cmdCatMap.remove(a);
                    }
                }
                return cat;
            }
        }
        return null;
    }

    public List<Category> getCategories()
    {
        return categories;
    }

    /**
     * Get commands that match the input String from all registered categories.
     * @param alias command name to search by.
     * @return Command found
     */
    public Command getCommand(String alias)
    {
        if (cmdCatMap.containsKey(alias))
        {
            return cmdCatMap.get(alias).findCommand(alias);
        }
        return null;
    }

    public void updateSlashCommands()
    {
        CommandListUpdateAction commands = Bot.jda.updateCommands();
        for(Category cat: categories)
        {
            for (Command cmd: cat.getCommandsDistinct())
            {
                commands.addCommands(cmd.updateSlashData());
            }
        }

        commands.queue();
        log.info("Updated Slash Command entries!");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) { return; }
        handleMessageCommandParsing(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getGuild() == null)
            return;

        handleSlashCommandParsing(event);
    }

    private void handleSlashCommandParsing(SlashCommandInteractionEvent event)
    {
        String cmd = event.getName();

        for (Category category : categories) {

            Command command = category.findCommand(cmd);
            if (command != null) {

                ArrayList<String> cmdArgs = new ArrayList<>();
                event.getOptions().forEach(option -> cmdArgs.add(option.getAsString()));

                SlashCommandContext ctx = new SlashCommandContext(event, command, cmdArgs);
                category.executeCommand(ctx);
            }
        }
    }

    private void handleMessageCommandParsing(MessageReceivedEvent event) {
        Message message = event.getMessage();

        String prefixGuild = ApiGuild.get(message.getGuild().getId()).getPrefix();



        String usedPrefix = prefixGuild;
        if (message.getContentRaw().startsWith(prefixGuild) || message.getMentions().isMentioned(Bot.jda.getSelfUser(), Message.MentionType.USER))
        {
            if (message.getMentions().isMentioned(Bot.jda.getSelfUser(), Message.MentionType.USER))
            {
                usedPrefix = String.format("<@%s>", Bot.jda.getSelfUser().getId());
            }

            // Split the message up into cmd, args
            // ["help", "debug", "timing"] for example

            ArrayList<String> cmdArgs = new ArrayList<>(List.of(message.getContentRaw().split(" ")));
            String cmd = cmdArgs.get(0).substring(usedPrefix.length());

            log.trace(cmd);

            cmdArgs.remove(0); // Remove the command from the args list
            // cmd = test
            for (Category category : categories) {

                Command command = category.findCommand(cmd);
                if (command != null) {
                    MessageContext ctx = new MessageContext(event, command, cmdArgs, prefixGuild, usedPrefix);
                    category.executeCommand(ctx);
                }
            }
        }
    }


}
