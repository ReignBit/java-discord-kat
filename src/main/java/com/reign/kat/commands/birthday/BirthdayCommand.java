package com.reign.kat.commands.birthday;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.converters.StringConverter;

public class BirthdayCommand extends Command {

    public BirthdayCommand(Category category) {
        super(new String[]{"birthday", "bday"}, "birthday" ,"Birthday configuration", category);
        /*
            !birthday config <tag> <value>
            !birthday add <member> <birthday>   // Also updates
            !birthday remove <member>
         */
        registerSubcommand(new BirthdayUtilityCommand());
        registerSubcommand(new BirthdayAddCommand(category));
        registerSubcommand(new BirthdayRemoveCommand(category));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {

    }
}
