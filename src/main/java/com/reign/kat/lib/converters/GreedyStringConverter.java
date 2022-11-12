package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.ContextEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreedyStringConverter extends Converter<String> {
    private static final Logger log = LoggerFactory.getLogger(StringConverter.class);

    public GreedyStringConverter(String argName, String description, String defaultString) {
        super(argName, description, defaultString, String.class);
        isGreedy = true;
    }

    @Override
    public Converter<String> convert(String toConvert, ContextEventAdapter event) throws IllegalArgumentException {
        set(toConvert);
        return this;
    }
}
