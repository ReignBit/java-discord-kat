package com.reign.kat.lib.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class VoiceCommandEvent
{
    public Guild guild;
    public Member author;
    public GuildChannel channel;
    public String parsedSpeech;
    public String wakeWord;

    public VoiceCommandEvent(Guild guild, Member author, GuildChannel channel,  String parsedSpeech, String wakeWord)
    {
        this.guild = guild;
        this.author = author;
        this.channel = channel;
        this.parsedSpeech = parsedSpeech;
        this.wakeWord = wakeWord;
    }
}
