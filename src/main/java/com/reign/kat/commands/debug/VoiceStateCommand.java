package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuildData;
import com.reign.kat.commands.voice.VoiceCategory;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.GuildConverter;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import com.reign.kat.lib.voice.GuildAudio;
import com.reign.kat.lib.voice.KatAudioManager;
import com.reign.kat.lib.voice.RequestedTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;


public class VoiceStateCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(VoiceStateCommand.class);

    public VoiceStateCommand() {
        super(new String[]{"vc"}, "vc","Print out current voice states.");
        addConverter(new GuildConverter(
                "guild",
                "Guild's voicestate to check",
                null
        ));
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
/*
        KatAudioManager kam = VoiceCategory.guildAudio;

        GuildAudio ga = kam.getGuildManager(params.get("guild"));


        EmbedBuilder eb = new GenericEmbedBuilder()
                .setTitle(params.get("guild").toString())
                .addField("STATUS", ga.getStatus().toString(), false)
                .addField("Current Track", Objects.toString(ga.scheduler.getNowPlaying(), "null"), false)
                .addField("Queue", ga.scheduler.getQueue().toString(), false);



        ctx.sendEmbeds(eb.build());
*/

    }

}

