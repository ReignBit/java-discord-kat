package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.GreedyStringConverter;
import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.voice.GuildAudio;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(PlayCommand.class);
    public PlayCommand()
    {
        super(new String[]{"play","p"},"play" ,"Add song to queue");
        addConverter(new GreedyStringConverter(
                "search",
                "Name of a song or URL",
                null
        ));
    }
    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        Guild guild = ctx.guild;
        GuildAudio guildAudio = VoiceCategory.guildAudio.getGuildManager(guild);
        log.info(String.valueOf(guildAudio));
        GuildVoiceState userVoiceState = ctx.author.getVoiceState();

        log.info("PlayCommand.execute({})", (String) args.get("search"));

        assert userVoiceState != null;
        if (userVoiceState.inAudioChannel())
        {
            guildAudio.setTextChannel(ctx.channel);
            guildAudio.loadSearch((VoiceChannel) userVoiceState.getChannel(), args.get("search"), ctx.author);
        }
        else
        {
            onUserNotInVoiceChannel(ctx);
        }



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
