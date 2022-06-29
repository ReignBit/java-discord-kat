package com.reign.kat.commands;

import com.reign.kat.commands.lib.Cog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.server.VoiceServerUpdateEvent;

public class ExampleCog extends Cog {
    private static final Logger log = LogManager.getLogger(Cog.class);

    public ExampleCog(DiscordApi api) {
        super(api);
        registerCommand(new DebugCommand());

        api.getServerById("438542169855361025").get().getChannelById("438542170366935043").get().asServerVoiceChannel().get().connect().thenAccept(audioConnection -> {
            audioConnection.setSelfDeafened(false);
            log.info(audioConnection.getAudioSource());

        });
        api.addVoiceServerUpdateListener(event -> onVoiceServerUpdate(api, event));
    }

    public void onVoiceServerUpdate(DiscordApi api, VoiceServerUpdateEvent event)
    {
        log.info(event.getEndpoint());
    }
}
