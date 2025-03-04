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

        setDatastore()
                .addField("test.array", new DatastoreField<>(new ArrayList<>()))
                .addField("test.check_enable", new DatastoreField<>(false))
                .addField("test.count", new DatastoreField<>(0));
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

        Command cmd = null;

        for (Category cat : Bot.commandHandler.getCategories())
        {
            cmd = findCommand(cat.getCommandsDistinct().stream().toList(), params.get("command"));
            if (cmd != null) { break; }
        }

        if (cmd != null) {
            try {
                Datastore d = null;
                for (Field f : cmd.getClass().getSuperclass().getDeclaredFields()) {
                    if (f.getName().equals("datastore")) {
                        d = (Datastore) f.get(cmd);
                    }
                }

                if (d == null) { throw new NoSuchFieldException(""); }


                StringBuilder sb = new StringBuilder();

                for (Iterator<Map.Entry<String, DatastoreField<?>>> it = d.getFields(); it.hasNext(); ) {
                    Map.Entry<String, DatastoreField<?>> df = it.next();
                    log.debug(df.getKey());
                    sb.append(String.format("%s : %s\n", df.getKey(), df.getValue().typeString()));
                    sb.append("\t");
                    sb.append(cmd.datastore.getField(df.getKey(), guild).toString().replace("\n", "\\n"));
                    sb.append("\n");
                }

                ctx.send(new GenericEmbedBuilder()
                        .setTitle("Datastore Structure (`" + cmd.getClass().getCanonicalName() + "`)")
                        .setDescription("```\n" + sb + "\n```")
                        .build());

            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("Command doesn't have a datastore field. This normally shouldn't happen...");
                ctx.sendError(new ExceptionEmbed()
                        .setDescription("Could not find Datastore for Command\n" + e)
                        .build());
            }
        } else {
            ctx.send(new GenericEmbedBuilder()
                    .setTitle("Datastore Structure")
                    .setDescription("No command found.")
                    .build());
        }
    }
}
