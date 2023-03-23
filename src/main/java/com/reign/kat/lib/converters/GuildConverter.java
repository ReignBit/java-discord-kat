package com.reign.kat.lib.converters;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.ContextEventAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildConverter extends Converter<Guild> {
    private static final Logger log = LoggerFactory.getLogger(GuildConverter.class);

    public GuildConverter(String argName, String description, Guild defaultGuild)
    {
        super(argName, description, defaultGuild, Guild.class);
    }

    @Override
    public Converter<Guild> convert(String toConvert, Context ctx) throws IllegalArgumentException{
        if (toConvert == null) { set(ctx.guild); return this; }
        if (toConvert.length() == 18)
        {
            Guild guild = ctx.jda.getGuildById(toConvert);
            if (guild != null) {
                set(guild);
                return this;
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("Tried to convert %s into Guild and failed!",toConvert));
        }
        return this;
    }
}
