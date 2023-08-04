package com.reign.kat.commands.voice;


import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import com.reign.kat.lib.voice.receive.VoiceRecognition;
import com.reign.kat.lib.voice.speech.Tokenizer;


public class VoiceCategory extends Category {
    public VoiceCategory()
    {
        GuildPlaylistPool.init();

        // TODO: Fix memory leak in speech recog. Disabled for now due to this!
        //Thread t = new Thread(VoiceRecognition::init);
        //t.start();
        //Tokenizer.init(1, "tokentable.json");

        setHelpMenuEmoji(":microphone:");
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
        registerCommand(new SeekCommand());

        registerCommand(new QueueCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new MoveCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new ClearPlaylistCommand());

        registerCommand(new LeaveCommand());
        registerCommand(new JoinCommand());

        registerCommand(new LyricsCommand());
        registerCommand(new LoopCommand());

        //registerCommand(new VoiceRecogDebugCommand());
    }
}
