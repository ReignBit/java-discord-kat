package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.exceptions.PreconditionFailedCommandException;
import com.reign.kat.lib.utils.PreCommandResult;
import com.reign.kat.lib.voice.KatAudioManager;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;



public class VoiceCategory extends Category {
    public VoiceCategory()
    {
        GuildPlaylistPool.init();

        setEmoji(":microphone:");
        addPrecommand(GuildPlaylist::ensureVoiceStatePreCommand);
        addPrecommand((c, args) ->
            {
                GuildPlaylistPool.get(c.guild.getIdLong()).getResponseHandler().setTextChannelID(c.channel.getIdLong());
                return null;
            }
        );

        registerCommand(new PlayCommand());
        registerCommand(new PlayNextCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new SeekCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new MoveCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new LeaveCommand());
        registerCommand(new JoinCommand());

        registerCommand(new LyricsCommand());
    }
}
