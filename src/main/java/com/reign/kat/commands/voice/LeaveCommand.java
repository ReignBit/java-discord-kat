package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.voice.GuildAudio;
import com.reign.kat.lib.voice.KatAudioManager;

public class LeaveCommand extends Command
{
    public LeaveCommand()
    {
        super(new String[]{"leave", "fuckoff", "disconnect"}, "leave", "Leaves the voice channel");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildAudio guildAudio = VoiceCategory.guildAudio.getGuildManager(ctx.guild);
        guildAudio.disconnect();
        KatAudioManager.deleteGuildManager(ctx.guild);
    }
}
