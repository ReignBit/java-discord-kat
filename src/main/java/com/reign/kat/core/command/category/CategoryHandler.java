package com.reign.kat.core.command.category;

import com.reign.kat.Bot;
import com.reign.kat.core.command.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * CommandCategories
 *
 * Contains all categories used by the Bot.
 */
public class CategoryHandler extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(CategoryHandler.class);
    private static final ArrayList<Category> categories = new ArrayList<>();

    public void addCategory(Category cat)
    {
        if (categories.contains(cat))
        {
            log.warn("Already registered Category {}", cat);
        }
        categories.add(cat);

    }

    public Category removeCategory(String name)
    {
        for (Category cat: categories)
        {
            if (cat.name.equalsIgnoreCase(name))
            {
                Category c = cat;
                categories.remove(cat);
                return c;
            }
        }
        return null;
    }

    public List<Category> getCategories()
    {
        return categories;
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
            String[] splitMessage = message.getContentRaw().split(" ");
            String cmd = splitMessage[0].substring(defaultPrefix.length());

            for (int i = 0; i < categories.size(); i++) {

                Command command = categories.get(i).findCommand(event, cmd);
                if (command != null) {
                    categories.get(i).executeCommand(event, command, splitMessage);
                }
            }
        }
    }
}
