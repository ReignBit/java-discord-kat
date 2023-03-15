package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.JoinedChannelEmbed;
import com.reign.kat.lib.exceptions.MissingArgumentCommandException;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;

public class JoinCommand extends Command
{
    public JoinCommand()
    {
        super(new String[]{"join", "connect"}, "join", "Joins the voice channel");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildPlaylist gp = GuildPlaylistPool.get(ctx.guild.getIdLong());

        gp.move(ctx.voiceChannel);
        ctx.sendEmbeds(new JoinedChannelEmbed().build());
    }
}
