package com.reign.kat.commands.voice;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.messages.VoiceMessages;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class JoinCommand extends Command
{
    public JoinCommand()
    {
        super(new String[]{"join", "j"}, "join", "Joins your voice channel");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        final GuildVoiceState gvc = ctx.author.getVoiceState();

        if (gvc != null && gvc.inAudioChannel())
        {
            Bot.jda.getDirectAudioController().connect(gvc.getChannel());
            ctx.reply(VoiceMessages.joinChannel(gvc.getChannel()));
            return;
        }

        ctx.reply(VoiceMessages.userNotInChannel());
    }
}
