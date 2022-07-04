package com.reign.kat.lib.converters;

import com.reign.kat.Bot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserConverter extends Converter<User> {
    private static final Logger log = LoggerFactory.getLogger(UserConverter.class);

    public UserConverter(String argName, String description, User defaultUser)
    {
        super(argName, description, defaultUser, User.class);
    }

    @Override
    public Converter<User> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException{
        if (toConvert == null) { set(null); return this; }
        if (toConvert.length() == 18)
        {
            log.info(toConvert);
            User user = Bot.jda.getUserById(toConvert);
            log.info("User = {}", user);
            if (user != null) {
                set(user);
                return this;
            }
        }
        else if (toConvert.startsWith("<@"))
        {
            // mention (<@123123123123123>)
            User user = Bot.jda.getUserById(toConvert.substring(2, toConvert.length()-1));
            if (user != null) {
                set(user);
                return this;
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("Tried to convert %s into User and failed!",toConvert));
        }
        return null;
    }
}
