package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.ContextEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntConverter extends Converter<Integer> {
    private static final Logger log = LoggerFactory.getLogger(IntConverter.class);

    public IntConverter(String argName, String description, Integer defaultInt) {
        super(argName, description, defaultInt, int.class);
    }

    @Override
    public Converter<Integer> convert(String toConvert, ContextEventAdapter event) throws IllegalArgumentException {
        try
        {
            set(Integer.parseInt(toConvert));
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException(String.format("Tried to convert %s into Integer and failed!",toConvert));
        }
        return this;
    }
}
