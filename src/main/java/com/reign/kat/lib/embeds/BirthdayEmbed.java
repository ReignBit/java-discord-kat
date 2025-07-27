package com.reign.kat.lib.embeds;

import net.dv8tion.jda.api.entities.Member;

public class BirthdayEmbed extends VoiceEmbed
{
    public BirthdayEmbed(Member member)
    {
        setDescription("Happy birthday!");
        setTitle(String.format("Happy Birthday %s!", member.getEffectiveName()));
    }
}
