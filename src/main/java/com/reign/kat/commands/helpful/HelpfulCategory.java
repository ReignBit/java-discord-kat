package com.reign.kat.commands.helpful;

import com.reign.kat.commands.debug.HelpCommand;
import com.reign.kat.lib.command.category.Category;

public class HelpfulCategory extends Category {
    public HelpfulCategory()
    {
        setEmoji(":information_source:");
        registerCommand(new HelpCommand());
    }
}
