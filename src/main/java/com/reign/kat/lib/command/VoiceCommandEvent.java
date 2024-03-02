package com.reign.kat.lib.command;

import com.reign.kat.lib.voice.speech.tokens.TokenResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class VoiceCommandEvent
{
    public Guild guild;
    public Member author;
    public GuildChannel channel;
    public TokenResult parsedSpeech;
    public String wakeWord;

    public VoiceCommandEvent(Guild guild, Member author, GuildChannel channel, TokenResult parsedSpeech, String wakeWord)
    {
        this.guild = guild;
        this.author = author;
        this.channel = channel;
        this.parsedSpeech = parsedSpeech;
        this.wakeWord = wakeWord;
    }
}
