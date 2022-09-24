package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.utils.Utilities;
import com.reign.kat.lib.voice.GuildAudio;
import com.reign.kat.lib.voice.RequestedTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NowPlayingCommand extends Command
{
    private static final Logger log = LoggerFactory.getLogger(NowPlayingCommand.class);

    // Character to print for the current time on the playing progress bar
    String currentTimeSymbol = ":white_circle:";
    // Character to print for the rest of the playing progress bar
    String trackSymbol = "\u2550";

    // Total size of the playing progress bar
    int totalBarSize = 22;

    public NowPlayingCommand()
    {
        super(new String[]{"nowplaying", "now", "np"}, "np", "Information about the current track");
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception
    {
        GuildAudio guildAudio = VoiceCategory.guildAudio.getGuildManager(ctx.guild);
        if (guildAudio.scheduler.isPlaying())
        {
            RequestedTrack track = guildAudio.scheduler.getNowPlaying();

            MessageEmbed e = buildNowPlayingEmbed(track);
            ctx.message.replyEmbeds(e).queue();
        }
    }

    private MessageEmbed buildNowPlayingEmbed(RequestedTrack track)
    {
        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Now Playing")
                .setDescription(
                        String.format(
                                "[**%s**](%s)\nRequested by: %s\n%s\n[%s:%s]",
                                track.getTrack().getInfo().title,
                                track.getTrack().getInfo().uri,
                                track.getRequester().getAsMention(),
                                buildProgressBar(track.getTrack()),
                                Utilities.timeConversion(track.getTrack().getPosition()),
                                Utilities.timeConversion(track.getTrack().getDuration())
                        )
                );

        return eb.build();
    }

    String buildProgressBar(AudioTrack track)
    {
        long totalTime = track.getDuration();
        int before = (int) (totalBarSize * track.getPosition() / (float)totalTime);
        return trackSymbol.repeat(before) + currentTimeSymbol + trackSymbol.repeat(totalBarSize - before);
    }
}