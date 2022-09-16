package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.utils.Utilities;
import com.reign.kat.lib.voice.GuildAudioManager;
import com.reign.kat.lib.voice.RequestedTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;


public class QueueCommand extends Command {

    public QueueCommand()
    {
        super(new String[]{"queue","q"},"queue" ,"Shows the playing queue");
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        Guild guild = ctx.guild;
        GuildAudioManager guildAudioManager = VoiceCategory.guildAudio.getGuildManager(guild);

        List<RequestedTrack> tracks = new ArrayList<>(guildAudioManager.scheduler.getQueue());

        sendQueueEmbed(ctx, generateQueueString(guildAudioManager.scheduler.getNowPlaying(), tracks, 0));

    }

    private String generateQueueString(RequestedTrack nowPlaying, List<RequestedTrack> tracks, int offset)
    {
        StringBuilder sb = new StringBuilder();

        if (nowPlaying != null)
        {
            sb.append("**:musical_note: Now playing**\n");
            sb.append(buildTrackStringLine(-1, nowPlaying));
            sb.append("\n");
        }

        for (int i = offset; i < tracks.size(); i++) {
            sb.append(buildTrackStringLine(i, tracks.get(i)));
        }
        if (sb.length() == 0)
        {
            sb.append("Noting is queued!");
        }
        return sb.toString();
    }

    private String buildTrackStringLine(int i, RequestedTrack track)
    {
        return String.format("%d. %s [%s] : %s\n",
                i+1,
                track.getTrack().getInfo().title,
                Utilities.timeConversion(track.getTrack().getDuration()),
                track.getRequester().getAsMention()
        );
    }

    private void sendQueueEmbed(Context ctx, String str)
    {
        EmbedBuilder eb = new VoiceEmbed()
                .setTitle(String.format("%s's Queue", ctx.guild.getName()))
                .setDescription(str);

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
