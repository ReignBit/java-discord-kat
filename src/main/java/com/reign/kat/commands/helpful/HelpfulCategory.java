package com.reign.kat.commands.helpful;

import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.data.DatastoreField;

import java.util.HashMap;

public class HelpfulCategory extends Category {
    public HelpfulCategory()
    {
        setHelpMenuEmoji(":information_source:");

        setDatastore()
                .addField("tag.tags", new DatastoreField<>(new HashMap<String, String>()));

        registerCommand(new HelpCommand());
        registerCommand(new ConfigParentCommand());

        registerCommand(new TagCommand());
        registerCommand(new TagUtilityCommand());
    }
}
