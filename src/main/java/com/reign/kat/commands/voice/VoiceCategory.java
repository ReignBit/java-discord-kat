package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.voice.KatAudioManager;



public class VoiceCategory extends Category {

    public static KatAudioManager guildAudio = new KatAudioManager();

    public VoiceCategory()
    {
        setEmoji(":microphone:");

        registerCommand(new PlayCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new MoveCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new SeekCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new NowPlayingCommand());
    }
}
