package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.voice.GuildAudioManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Objects;

public class PlayCommand extends Command {

    public PlayCommand()
    {
        super(new String[]{"play","p"},"play" ,"Add song to queue");
        addConverter(new StringConverter(
                "search",
                "Name of a song or URL",
                null
        ));
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        Guild guild = ctx.guild;
        GuildAudioManager guildAudioManager = VoiceCategory.guildAudio.getGuildManager(guild);

        GuildVoiceState userVoiceState = ctx.author.getVoiceState();

        assert userVoiceState != null;
        if (userVoiceState.inAudioChannel())
        {
            guildAudioManager.loadSearch((VoiceChannel) userVoiceState.getChannel(), args.get("search"));
        }



    }
}
