package com.reign.kat.commands.helpful;


import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.ParentCommand;
import com.reign.kat.lib.utils.PermissionGroupType;

public class ConfigParentCommand extends ParentCommand {
    public ConfigParentCommand() {
        super(
                new String[] {"config"},
                "config",
                "Customize settings for your server."
        );
        setRequiredPermissionGroup(PermissionGroupType.ADMINISTRATOR);
        registerSubcommand(new PrefixCommand());
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
    }
}