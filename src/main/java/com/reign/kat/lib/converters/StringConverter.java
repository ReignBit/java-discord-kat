package com.reign.kat.lib.converters;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringConverter extends Converter<String> {
    private static final Logger log = LoggerFactory.getLogger(StringConverter.class);

    public StringConverter(String argName, String description, String defaultString) {
        super(argName, description, defaultString, String.class);
    }

    @Override
    public Converter<String> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException {
        set(toConvert);
        return this;
    }
}
