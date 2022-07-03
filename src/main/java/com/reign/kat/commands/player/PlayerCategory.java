package com.reign.kat.commands.player;

import com.reign.kat.lib.command.category.Category;

public class PlayerCategory extends Category {
    public PlayerCategory()
    {
        setEmoji(":microphone:");
        registerCommand(new PlayCommand());
    }

}
