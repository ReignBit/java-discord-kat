package com.reign.kat.commands.debug;

import com.reign.api.kat.models.ApiGuildData;
import com.reign.kat.commands.voice.VoiceCategory;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.voice.GuildAudio;
import com.reign.kat.lib.voice.KatAudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class VoiceStateCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(VoiceStateCommand.class);

    public VoiceStateCommand() {
        super(new String[]{"vc"}, "vc","Print out current voice states.");
    }


    @Override
    public void execute(Context ctx, CommandParameters params) {
        KatAudioManager kam = VoiceCategory.guildAudio;

        for (Map.Entry<String, GuildAudio> set :
                kam.all())
        {
            String guildId = set.getKey();
            GuildAudio audio = set.getValue();

            ctx.channel.sendMessage(String.format("GID %s : %s", guildId, audio.scheduler.queue.toString())).queue();
        }
    }

}

