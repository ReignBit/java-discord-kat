package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.embeds.VoiceEmbed;
import com.reign.kat.lib.utils.Utilities;
import com.reign.kat.lib.voice.GuildAudio;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SeekCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(SeekCommand.class);
    public SeekCommand()
    {
        super(new String[]{"seek"},"seek" ,"Seek through the current track");
        addConverter(new StringConverter(
                "position",
                "Position in the track to seek to.",
                "0"
        ));
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        Guild guild = ctx.guild;
        GuildAudio guildAudio = VoiceCategory.guildAudio.getGuildManager(guild);

        GuildVoiceState userVoiceState = ctx.author.getVoiceState();
        assert userVoiceState != null;
        if (userVoiceState.inAudioChannel())
        {
            guildAudio.setTextChannel(ctx.channel);
            seek(args.get("position"), guildAudio, ctx);
        }
        else
        {
            onUserNotInVoiceChannel(ctx);
        }
    }

    private void seek(String timestamp, GuildAudio g, Context ctx)
    {
        Long position = Utilities.stringToTimeConversion(timestamp);

        if (g.scheduler.getNowPlaying() == null)
        {
            onNotPlaying(ctx);
            return;
        }

        AudioTrack now = g.scheduler.getNowPlaying().getTrack();
        if (position > now.getDuration() || position < 0)
        {
            onInvalidTimestamp(timestamp, ctx);
            return;
        }

        g.scheduler.getNowPlaying().track.setPosition(position);

        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Seeked song")
                .setDescription(String.format("Seeked to %s", timestamp));
        ctx.message.replyEmbeds(eb.build()).queue();
    }

    private void onNotPlaying(Context ctx)
    {
        EmbedBuilder eb = new VoiceEmbed()
                .setTitle("Nothing is playing!");

        ctx.message.replyEmbeds(eb.build()).queue();
    }

    private void onInvalidTimestamp(String timestamp, Context ctx)
    {
        EmbedBuilder eb = new ExceptionEmbedBuilder(
                ":x:",
                String.format("Unable to seek to %s", timestamp),
                "The time requested is invalid!"
        );

        ctx.message.replyEmbeds(eb.build()).queue();
    }

    /**
     * Called when a user tries to play music whilst not in a voice channel
     * @param ctx Context of the call.
     */
    private void onUserNotInVoiceChannel(Context ctx)
    {
        EmbedBuilder eb = new ExceptionEmbedBuilder(
                ":x:",
                "Could not join voice channel",
                "You must be connected to a voice channel to play music!"
        );

        ctx.message.replyEmbeds(eb.build()).queue();
    }

    /**
     * Called when the bot is unable to join the user's voice channel, normally due to insufficient bot permissions.
     * @param ctx Context of the call.
     */
    private void onUnableToJoinUserVoiceChannel(Context ctx)
    {
        EmbedBuilder eb = new ExceptionEmbedBuilder(
                ":x:",
                "Could not join voice channel",
                "I have insufficient permissions to join your voice channel!"
        );

        ctx.message.replyEmbeds(eb.build()).queue();
    }
}
