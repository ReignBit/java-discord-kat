package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.data.Datastore;
import com.reign.kat.lib.command.data.DatastoreField;
import com.reign.kat.lib.converters.GreedyStringConverter;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class DatastoreTestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(DatastoreTestCommand.class);

    public DatastoreTestCommand() {
        super(new String[]{"ds"}, "datastore" ,"test datastore functionality.");
        addConverter(new GreedyStringConverter("command", "Name of the command to check data structure for", ""));
    }

    public static Command findCommand(List<Command> commands, String name) {
        // Iterate over the list of commands
        for (Command command : commands) {
            // Check if the current command matches the target name
            if (command.getClass().getSimpleName().equals(name)) {
                log.debug("FOUND COMMAND");
                return command;
            }
            // Recursively search through children
            Command result = findCommand(command.getChildren(), name);
            if (result != null) {
                return result;  // Found the command in the children
            }
        }
        return null;  // Not found in this branch
    }

    @Override
    public void execute(Context ctx, CommandParameters params) {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());
    }
}
