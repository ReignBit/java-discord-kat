package com.reign.kat.commands.fun.emote;

import com.reign.kat.commands.debug.HelpCommand;
import com.reign.kat.commands.debug.TestCommand;
import com.reign.kat.core.command.category.Category;

public class EmoteCategory extends Category {
    public EmoteCategory()
    {
        setEmoji(":tada:");
        registerCommand(new SlapCommand());
    }
}
