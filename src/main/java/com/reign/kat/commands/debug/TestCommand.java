package com.reign.kat.commands.debug;

import com.reign.kat.core.converters.Converter;
import com.reign.kat.core.converters.MemberConverter;
import com.reign.kat.core.converters.UserConverter;
import com.reign.kat.core.command.Command;
import com.reign.kat.core.command.CommandParameters;
import com.reign.kat.core.command.Context;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

    public TestCommand() {
        super(new String[]{"test"}, "I guess this would be the description?");
        addConverter(new MemberConverter(
                "exampleArgument",
                "This is where an important argument would go",
                false
        ));
    }


    // !help @user
    @Override
    public void execute(Context ctx, CommandParameters params) {
        Converter<?> t = params.params.get(0);
        Member u = t.get();

        ctx.channel.sendMessage(String.format("Hello there %s", u.getEffectiveName())).queue();
    }
}
