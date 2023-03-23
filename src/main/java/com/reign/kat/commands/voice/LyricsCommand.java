package com.reign.kat.commands.voice;

import com.reign.api.genius.GeniusApi;
import com.reign.api.genius.GeniusSong;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.exceptions.PreconditionFailedCommandException;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class LyricsCommand extends Command
{
    public LyricsCommand()
    {
        super(new String[]{"lyrics"}, "lyrics", "Attempts to get lyrics a song");
        addConverter(new StringConverter(
                "songname",
                "Song name. Uses current playing track name if blank",
                ""
        ));
        setShowTyping(true);
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());

        String songName = args.get("songname");

        if (songName.equals(""))
        {
            if (playlist.nowPlaying() != null)
            {
                // Remove anything after brackets to help search accuracy
                songName = playlist.nowPlaying().title.split("\\[")[0].split("\\(")[0];
                if (songName.length() < 10)
                {
                    // Add the track author name to short titles to try and
                    // help with searching for correct track name
                    songName += " " + playlist.nowPlaying().author;
                }
            }
            else
            {
                throw new PreconditionFailedCommandException("No song provided and no track is currently playing.");
            }
        }

        GeniusSong song = GeniusApi.lyrics(songName);

        if (song != null)
        {
            List<MessageEmbed> embeds = new ArrayList<>();
            boolean first = true;
            for (String segment :
                    song.lyricSegments())
            {

                embeds.add(new VoiceEmbed()
                        .setTitle(first ? String.format("Lyrics for %s by %s", song.title(), song.artist()) : "")
                        .setDescription(segment)
                        .setFooter("Lyrics provided by Genius.com and may not be accurate").build());
                first = false;
            }

            ctx.send(embeds.toArray(MessageEmbed[]::new));
            return;
        }

        ctx.send(new VoiceEmbed()
                .setTitle("No lyrics found :(")
                .setDescription(String.format("Could not find any lyrics for `%s`", songName))
                .setFooter("Lyrics provided by Genius.com and may not be accurate").build());

    }
}
