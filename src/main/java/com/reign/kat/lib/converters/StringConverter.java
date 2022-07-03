package com.reign.kat.lib.converters;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringConverter extends Converter<String> {
    private static final Logger log = LoggerFactory.getLogger(StringConverter.class);

    public StringConverter(String argName, String description, boolean optional) {
        super(argName, description, optional);
    }

    @Override
    public Converter<String> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException {
        log.info(toConvert);
        set(toConvert);
        return this;
    }
}
