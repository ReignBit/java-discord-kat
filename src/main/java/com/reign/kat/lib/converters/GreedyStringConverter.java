package com.reign.kat.lib.converters;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreedyStringConverter extends Converter<String> {
    private static final Logger log = LoggerFactory.getLogger(StringConverter.class);

    public GreedyStringConverter(String argName, String description, String defaultString) {
        super(argName, description, defaultString, String.class);
        isGreedy = true;
    }

    @Override
    public Converter<String> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException {
        set(toConvert);
        return this;
    }
}
