package com.reign.kat.commands.voice;


import com.reign.kat.lib.command.ButtonInteractionContext;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import com.reign.kat.lib.voice.receive.VoiceRecognition;
import com.reign.kat.lib.voice.speech.Tokenizer;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VoiceCategory extends Category {

    private static final Logger log = LoggerFactory.getLogger(VoiceCategory.class);
    public VoiceCategory()
    {
        GuildPlaylistPool.init();

        // TODO: Fix memory leak in speech recog. Disabled for now due to this!
        Thread t = new Thread(VoiceRecognition::init);
        t.start();
        Tokenizer.init(1, "tokentable.json");

        setHelpMenuEmoji(":microphone:");
        addPrecommand(GuildPlaylist::ensureVoiceStatePreCommand);
        addPrecommand((c, args) ->
            {
                GuildPlaylistPool.get(c.guild.getIdLong()).getResponseHandler().setTextChannelID(c.channel.getIdLong());
                return null;
            }
        );



        registerCommand(new PlayCommand());
        registerCommand(new PlayNextCommand());

        registerCommand(new PauseCommand());
        registerCommand(new StopCommand());

        registerCommand(new SkipCommand());
        registerCommand(new SeekCommand());

        registerCommand(new QueueCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new MoveCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new ClearPlaylistCommand());

        registerCommand(new LeaveCommand());
        registerCommand(new JoinCommand());

        registerCommand(new LyricsCommand());
        registerCommand(new LoopCommand());

        registerCommand(new VoiceRecogDebugCommand());
    }

    @Override
    public void onHourEvent()
    {
        long usedMB = (Runtime.getRuntime().totalMemory() / Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        long totalMB = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        log.debug(String.format("Used Memory: %dMB/%dMB", usedMB, totalMB));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        if (event.getComponentId().equals("play-again"))
        {
            Pattern p = Pattern.compile("(?<=\\()https:\\/\\/.+(?=\\))");
            Matcher m = p.matcher(Objects.requireNonNull(event.getMessage().getEmbeds().get(0).getDescription()));
            if (m.find()) {}
            String[] link = {m.group()};
            log.debug(Arrays.toString(link));
            Context ctx = new ButtonInteractionContext(
                    event,
                    findCommand("play"),
                    List.of(link),
                    "!",
                    "!"
            );

            executeCommand(ctx);

        }


    }
}
