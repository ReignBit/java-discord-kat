package com.reign.kat.lib.snowflake;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.entities.User;

public class UserId
{
    public final long id;

    public UserId(long id)
    {
        this.id = id;
    }

    public User get()
    {
        return Bot.jda.getUserById(id);
    }
}
