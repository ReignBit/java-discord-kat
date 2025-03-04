package com.reign.kat.commands.helpful;

import com.reign.kat.lib.command.category.Category;

public class HelpfulCategory extends Category {
    public HelpfulCategory()
    {
        setHelpMenuEmoji(":information_source:");
        registerCommand(new HelpCommand());
        registerCommand(new ConfigParentCommand());

        registerCommand(new TagCommand());
        registerCommand(new TagUtilityCommand());
    }
}
