package com.reign.kat.lib.snowflake;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class MemberId
{
    public final long id;
    public final long guildId;

    public MemberId(long guildId, long id)
    {
        this.guildId = guildId;
        this.id = id;
    }

    public static MemberId fromMember(Member member)
    {
        return new MemberId(member.getGuild().getIdLong(), member.getIdLong());
    }

    public Member get()
    {
        return Objects.requireNonNull(Bot.jda.getGuildById(guildId)).getMemberById(id);
    }
}
