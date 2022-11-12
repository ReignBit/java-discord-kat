package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.ContextEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanConverter extends Converter<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(BooleanConverter.class);

    public BooleanConverter(String argName, String description, boolean defaultBoolean) {
        super(argName, description, defaultBoolean, boolean.class);
    }

    @Override
    public Converter<Boolean> convert(String toConvert, ContextEventAdapter event) throws IllegalArgumentException {
        try
        {
            set(Boolean.parseBoolean(toConvert));
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException(String.format("Tried to convert %s into Boolean and failed!",toConvert));
        }
        return this;
    }
}
