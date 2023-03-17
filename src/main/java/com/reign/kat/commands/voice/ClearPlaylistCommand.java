package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.JoinedChannelEmbed;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;

public class ClearPlaylistCommand extends Command
{
    public ClearPlaylistCommand()
    {
        super(new String[]{"clear", "clearplaylist"}, "clear", "Clears the playlist");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        playlist.getQueue().clear();
        ctx.sendEmbeds(new VoiceEmbed().setTitle("Cleared the playlist").build());
    }
}
