package com.reign.kat.commands.voice;

import com.reign.kat.lib.Config;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.JoinedChannelEmbed;
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
                        .append("\nIS LOADED: ").append(VoiceRecognition.isRecognizerReady())
                .append("\n");
        playlist.audioRecvManager.handler.users.forEach((id, info) -> sb.append(String.format("%d: speaking: %b, lastSpoke: %d, dataLen: %d\n",
                id, info.speaking, info.lastSpoken, info.buffer.size())));

        ctx.sendMessage(sb.toString());
    }
}
