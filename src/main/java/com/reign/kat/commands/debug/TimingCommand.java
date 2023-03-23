package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.converters.BooleanConverter;
import com.reign.kat.lib.utils.DiscordColor;
import com.reign.kat.lib.utils.stats.BotStats;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

public class TimingCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(TimingCommand.class);

    public TimingCommand() {
        super(new String[]{"timing"}, "timing", "Show average timing for each command");
        addConverter(new BooleanConverter(
                "outputToConsole",
                "Output full report to console?",
                false
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) {
        HashMap<Command, Float> avgTimings = BotStats.getAvgExecutionTime();

        StringBuilder sb = new StringBuilder();
        sb.append("```\n");
        for(Iterator<String> it = BotStats.buildReport(); it.hasNext();)
        {
            String ln = it.next();
            sb.append(ln).append("\n");
        }
        sb.append("\n```");

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(":stopwatch: BotStats Command Timings")
                .setColor(DiscordColor.BACKGROUND_GREY)
                .setDescription(sb.toString());



        ctx.channel.sendMessageEmbeds(eb.build()).queue();

        if (args.get("outputToConsole"))
        {
            BotStats.reportToConsole();
        }
    }
}
