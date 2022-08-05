package com.reign.kat.commands.fun.emote;

import com.reign.kat.lib.command.category.Category;

public class EmoteCategory extends Category {
    public EmoteCategory()
    {
        setEmoji(":tada:");
        registerCommand(new SlapCommand());
    }
}
