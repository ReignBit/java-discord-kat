package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ButtonTestCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(ButtonTestCommand.class);

    public ButtonTestCommand() {
        super(new String[]{"bt"}, "buttontest" ,"Shows embed with some test buttons and handlers");
    }



    @Override
    public void execute(Context ctx, CommandParameters params) {
        EmbedBuilder embed = new EmbedBuilder().setTitle(":robot: Debug Message");
        ctx.reply(embed.build());
    }
}
