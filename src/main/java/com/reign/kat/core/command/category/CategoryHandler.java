package com.reign.kat.core.command.category;

import com.reign.kat.Bot;
import com.reign.kat.core.command.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * CommandCategories
 *
 * Contains all categories used by the Bot.
 */
public class CategoryHandler extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(CategoryHandler.class);
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
        for (Command cmd: cat.getCommands()) {
            String[] aliases = cmd.getAliases();
            for (String a: aliases) {
                cmdCatMap.put(a, cat);
            }
        }

    }

    public Category removeCategory(String name)
    {
        for (Category cat: categories)
        {
            if (cat.name.equalsIgnoreCase(name))
            {
                categories.remove(cat);

                for (Command cmd: cat.getCommands()) {
                    String[] aliases = cmd.getAliases();
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
            return cmdCatMap.get(alias).getCommand(alias);
        }
        return null;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) { return; }

        Message message = event.getMessage();
        String defaultPrefix = Bot.config.getDefaultPrefix();
        if (message.getContentRaw().startsWith(defaultPrefix))
        {
            // Split the message up into cmd, args
            ArrayList<String> splitMessage = new ArrayList<>(List.of(message.getContentRaw().split(" ")));
            String cmd = splitMessage.get(0).substring(defaultPrefix.length());
            splitMessage.remove(0); // Remove the command from the args list


            // cmd = test
            for (Category category : categories) {

                Command command = category.findCommand(event, cmd);
                if (command != null) {
                    category.executeCommand(event, command, splitMessage);
                }
            }
        }
    }
}
