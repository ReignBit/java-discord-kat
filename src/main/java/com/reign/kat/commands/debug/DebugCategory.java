package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.utils.DiscordPermission;
import com.reign.kat.lib.utils.PermissionGroupType;

public class DebugCategory extends Category {
    public DebugCategory()
    {
//        setRequiredPermissionGroups(PermissionGroupType.ADMINISTRATOR);
//        setRequiredDiscordPermissions(DiscordPermission.MANAGE_MESSAGES | DiscordPermission.MOVE_MEMBERS);
        setEmoji(":gear:");
        registerCommand(new TestCommand());
        registerCommand(new HelpCommand());
        registerCommand(new KillCommand());
        registerCommand(new DebugParentCommand());
    }

}
