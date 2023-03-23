package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.ContextEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringConverter extends Converter<String> {
    private static final Logger log = LoggerFactory.getLogger(StringConverter.class);

    public StringConverter(String argName, String description, String defaultString) {
        super(argName, description, defaultString, String.class);
    }

    @Override
    public Converter<String> convert(String toConvert, Context ctx) throws IllegalArgumentException {
        set(toConvert);
        return this;
    }
}
