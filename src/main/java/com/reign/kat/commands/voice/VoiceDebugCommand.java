package com.reign.kat.commands.voice;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.messages.VoiceMessages;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class VoiceDebugCommand extends Command
{
    public VoiceDebugCommand()
    {
        super(new String[]{"vdebug", "stats"}, "vdebug", "Show lavalink status");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        ctx.reply(VoiceMessages.lavalinkStatus(Bot.lavalink, ctx.author));
    }
}
