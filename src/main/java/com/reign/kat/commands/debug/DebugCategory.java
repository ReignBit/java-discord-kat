package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.category.Category;

public class DebugCategory extends Category {
    public DebugCategory()
    {
        setEmoji(":gear:");
        registerCommand(new TestCommand());
        registerCommand(new HelpCommand());
        registerCommand(new KillCommand());
    }

}
