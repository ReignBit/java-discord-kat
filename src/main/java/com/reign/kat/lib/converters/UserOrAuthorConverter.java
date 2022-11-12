package com.reign.kat.lib.converters;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.ContextEventAdapter;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserOrAuthorConverter extends Converter<User> {
    private static final Logger log = LoggerFactory.getLogger(UserOrAuthorConverter.class);

    public UserOrAuthorConverter(String argName, String description, User defaultUser)
    {
        super(argName, description, defaultUser, User.class);
        setOptional(true);
    }

    @Override
    public Converter<User> convert(String toConvert, ContextEventAdapter event) throws IllegalArgumentException{
        if (toConvert == null) { set(event.getAuthor()); return this; }
        if (toConvert.length() == 18)
        {
            log.debug(toConvert);
            User user = Bot.jda.getUserById(toConvert);
            log.debug("User = {}", user);
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
            set(event.getAuthor());
            return this;
        }
        throw new IllegalArgumentException(String.format("Tried to convert %s into User and failed!",toConvert));
    }
}
