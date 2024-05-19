package com.reign.kat.commands.debug;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.converters.GuildConverter;
import com.reign.kat.lib.utils.DiscordColor;
import com.reign.kat.lib.utils.stats.GuildPlaylistStats;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import de.vandermeer.asciitable.AsciiTable;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;


public class VoiceStateCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(VoiceStateCommand.class);

    public VoiceStateCommand() {
        super(new String[]{"vc"}, "vc","Print out current voice states.");
//        addConverter(new GuildConverter(
//                "guild",
//                "Guild's voicestate to check",
//
//        ));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {

        var report = GuildPlaylistStats.get();

        AsciiTable table = new AsciiTable();
        table.getContext().setWidth(55);
        table.addRule();
        table.addRow("Guild ID", "Is Playing", "Queue Size");
        for (var stat :
                report.stats())
        {
            table.addRule();
            table.addRow(stat.id(), stat.isPlaying(), stat.queueSize());
        }
        table.addRule();
        table.addRow(null, null, "Total Playing: " + report.totalPlaying());
        table.addRule();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(":stopwatch: BotStats Command Timings")
                .setColor(DiscordColor.BACKGROUND_GREY)
                .setDescription("```\n" + table.render(55) + "\n```");

        ctx.send(eb.build());
    }

}

