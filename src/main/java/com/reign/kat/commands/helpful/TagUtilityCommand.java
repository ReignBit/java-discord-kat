package com.reign.kat.commands.helpful;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.data.DatastoreField;
import com.reign.kat.lib.converters.GreedyStringConverter;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import com.reign.kat.lib.exceptions.MissingArgumentCommandException;

import java.util.HashMap;
import java.util.Map;

public class TagUtilityCommand extends Command {


    public TagUtilityCommand() {
        super(new String[]{"tag"}, "tag", "Add/Remove tags");

        addConverter(new StringConverter("operation", "add/remove", "add"));
        addConverter(new StringConverter("tagName", "Name of the tag", ""));
        addConverter(new GreedyStringConverter("text", "Tag text", ""));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) throws Exception {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());

        Map<String, String> tags = category.datastore.getField("tag.tags", guild);

        if (((String) params.get("text")).isEmpty()) {
            throw new MissingArgumentCommandException("No tag text provided.");
        }

        if (((String) params.get("tagName")).isEmpty()) {
            throw new MissingArgumentCommandException("No tag name provided.");
        }


        switch ((String) params.get("operation")) {
            case "add" -> {
                tags.put(params.get("tagName"), params.get("text"));
                ctx.send("Added tag.");
            }
            case "remove" ->{
                tags.remove((String) params.get("tagName"));
                ctx.send("Removed tag.");

            }
            default ->
                throw new MissingArgumentCommandException("Invalid operation (add/remove).");
        }

        category.datastore.updateField("tag.tags", tags, guild);
    }
}
