package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.embeds.ExceptionEmbedBuilder;
import com.reign.kat.lib.voice.GuildAudio;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;


public class SkipCommand extends Command {

    public SkipCommand()
    {
        super(new String[]{"skip","s"},"skip" ,"Skips the playing song");
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
            guildAudio.skip();
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
