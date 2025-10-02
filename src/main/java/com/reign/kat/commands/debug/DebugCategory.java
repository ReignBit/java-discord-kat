package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.data.DatastoreField;
import com.reign.kat.lib.utils.PermissionGroupType;

import java.util.ArrayList;

public class DebugCategory extends Category {
    public DebugCategory()
    {
        setRequiredPermissionGroup(PermissionGroupType.ADMINISTRATOR);
        //setRequiredDiscordPermissions(0);
        setHelpMenuEmoji(":gear:");
        setDatastore()
                .addField("test.array", new DatastoreField<>(new ArrayList<>()))
                .addField("test.check_enable", new DatastoreField<>(false))
                .addField("test.count", new DatastoreField<>(0));
        registerCommand(new DebugParentCommand());
    }

}
