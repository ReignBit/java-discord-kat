package com.reign.kat.lib.voice.music;

import com.reign.kat.Bot;
import com.reign.kat.lib.Config;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicManager
{
    private static final Logger log = LoggerFactory.getLogger(MusicManager.class);
    private static final int SESSION_INVALID = 4006;

    public static void registerListeners(LavalinkClient client)
    {
        client.on(WebSocketClosedEvent.class).subscribe((event) -> {
           if (event.getCode() == SESSION_INVALID)
           {
               final var guildId = event.getGuildId();
               final var guild = Bot.jda.getGuildById(guildId);

               if (guild == null) { return; }

               final var connectedChannel = guild.getSelfMember().getVoiceState().getChannel();
               if (connectedChannel == null) { return; }

               Bot.jda.getDirectAudioController().reconnect(connectedChannel);
           }
        });

        client.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            log.info("Node '{}' is ready, session id: {}",
                    node.getName(),
                    event.getSessionId()
            );
        });

        client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            log.info(
                    "Node '{}' has stats, current players: {}/{} (link count {})",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size()
            );
        });

        client.on(EmittedEvent.class).subscribe((event) -> {
            if (event instanceof TrackStartEvent) {
                log.info("Is a track start event!");
            }

            final var node = event.getNode();

            log.info(
                    "Node '{}' emitted event: {}",
                    node.getName(),
                    event
            );
        });
    }

    public static void registerNodes(LavalinkClient client)
    {
        LavalinkNode node = client.addNode(
                new NodeOptions.Builder()
                        .setName(Config.VOICE_LAVALINK_NAME)
                        .setServerUri(Config.VOICE_LAVALINK_URI)
                        .setPassword(Config.VOICE_LAVALINK_PASSWD)
                        .build()
        );
        node.on(TrackStartEvent.class).subscribe((event) -> {
            final LavalinkNode n = event.getNode();
            log.info("{}: track started: {}",
                    n.getName(),
                    event.getTrack().getInfo()
            );
        });
    }

    /**
     * Check if Bot and Member voice states are correct:
     *  - Member in voice channel (and in same one as bot if bot has a state)
     * @param client
     * @param member
     * @return ID of the target AudioChannel if bot needs to move, otherwise 0. -1 if invalid states
     */
    public static boolean joinHelper(LavalinkClient client, Member member)
    {
        // Member not in voice
        if (member.getVoiceState() == null || !member.getVoiceState().inAudioChannel())
        {
            log.debug("User not in voice channel");
            return false;
        }

        GuildVoiceState vc = member.getGuild().getSelfMember().getVoiceState();
        if (vc.inAudioChannel())
        {
            log.debug("Bot in voice?");
            AudioChannel selfChannel = vc.getChannel();
            // Member not in same voice channel as bot
            log.debug(String.valueOf(selfChannel == member.getVoiceState().getChannel()));
            return selfChannel == member.getVoiceState().getChannel();
        }

        log.debug("Joining channel {}", member.getVoiceState().getChannel());
        Bot.jda.getDirectAudioController().connect(member.getVoiceState().getChannel());

        return true;
    }


}
