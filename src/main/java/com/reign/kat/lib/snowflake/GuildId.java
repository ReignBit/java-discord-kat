package com.reign.kat.lib.snowflake;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class GuildId
{
    public final long id;

    public GuildId(long id)
    {
        this.id = id;
    }

    public Guild get()
    {
        return Bot.jda.getGuildById(id);
    }
}
