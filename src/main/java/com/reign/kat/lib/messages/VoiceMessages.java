package com.reign.kat.lib.messages;

import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.VoiceEmbed;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.LoadFailed;
import dev.arbjerg.lavalink.client.player.NoMatches;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;

public class VoiceMessages
{
    public static MessageEmbed lavalinkStatus(LavalinkClient client, Member member)
    {
        EmbedBuilder v = new VoiceEmbed().setTitle("Lavalink Status");
        client.getNodes().forEach((node) -> {
            String d = String.format("Node '%s' has stats, current player: %s (link count %d)",
                    node.getName(),
                    node.getPlayer(member.getGuild().getIdLong()),
                    client.getLinks().size()
            );
            v.addField(node.getName(), d, false);
        });

        return v.build();
    }

    public static MessageEmbed joinChannel(Channel channel)
    {

        return new VoiceEmbed()
                .setTitle("Joining " + channel.getAsMention())
                .build();
    }

    public static MessageEmbed userNotInChannel()
    {
        return new VoiceEmbed()
                .setTitle("Could not join channel")
                .setDescription("You must be in a valid voice channel to use this command.")
                .build();
    }

    public static MessageEmbed loadFailed(LoadFailed event)
    {

        return new ExceptionEmbed()
                .setTitle("Failed to load track")
                .setDescription("```\n" + event.getException() + "\n```")
                .build();

    }

    public static MessageEmbed noMatches(String query)
    {
        return new VoiceEmbed()
                .setTitle("No Matches found!")
                .setDescription("Could not find anything matching `" + query + "`.")
                .build();

    }
}
