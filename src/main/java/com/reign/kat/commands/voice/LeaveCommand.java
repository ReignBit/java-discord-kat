package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.voice.GuildAudioManager;

public class LeaveCommand extends Command
{
    public LeaveCommand()
    {
        super(new String[]{"leave", "fuckoff", "disconnect"}, "leave", "Leaves the voice channel");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildAudioManager guildAudioManager = VoiceCategory.guildAudio.getGuildManager(ctx.guild);
        guildAudioManager.disconnect();
    }
}
