package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.BooleanConverter;
import com.reign.kat.lib.utils.DiscordColor;
import com.reign.kat.lib.utils.stats.BotStats;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;
import java.util.Map;

public class TimingCommand extends Command {

    public TimingCommand() {
        super(new String[]{"timing"}, "timing", "Show average timing for each command");
        addConverter(new BooleanConverter(
                "outputToConsole",
                "Output full report to console?",
                true
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) {
        HashMap<Command, Float> avgTimings = BotStats.getAvgExecutionTime();


        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(":stopwatch: BotStats Command Timings")
                .setColor(DiscordColor.BACKGROUND_GREY)
                .setDescription(
                        String.format("Average command execution time based on the last %s executions", BotStats.HISTORY_SIZE)
                );

        for (Map.Entry<Command, Float> entry: avgTimings.entrySet())
        {
            Command c = entry.getKey();
            float f = entry.getValue();
            eb.addField(c.getClass().getSimpleName(), String.format("%.2fms", f), true);
        }

        ctx.channel.sendMessageEmbeds(eb.build()).queue();

        if (args.get("outputToConsole"))
        {
            BotStats.reportToConsole();
        }
    }
}
