package com.reign.kat.commands.voice;

import com.reign.kat.lib.Config;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import com.reign.kat.lib.voice.receive.VoiceRecognition;

import java.util.Arrays;

public class VoiceRecogDebugCommand extends Command
{
    public VoiceRecogDebugCommand()
    {
        super(new String[]{"vrd", "voicerecogdebug"}, "voicerecogdebug", "");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        StringBuilder sb = new StringBuilder();
        sb.append("MODEL: ").append(Config.SPEECH_RECOGNITION_MODEL_NAME)
                        .append("\nWAKE WORDS: ").append(Arrays.toString(Config.SPEECH_RECOGNITION_WAKE_WORDS))
                        .append("\nIS LOADED: ").append(VoiceRecognition.isModelReady())
                .append("\n");
        playlist.audioRecvManager.users.forEach((id, info) -> sb.append(String.format("%d: dataLen: %d\n",
                id, info.buffer.size())));

        long usedMB = (Runtime.getRuntime().totalMemory() / Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        long totalMB = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        sb.append(String.format("Used Memory: %dMB/%dMB", usedMB, totalMB));

        ctx.send(sb.toString());
    }
}
