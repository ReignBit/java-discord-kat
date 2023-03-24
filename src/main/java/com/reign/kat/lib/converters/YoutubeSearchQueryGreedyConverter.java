package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoutubeSearchQueryGreedyConverter extends Converter<String> {
    public static final Logger log = LoggerFactory.getLogger(StringConverter.class);

    public YoutubeSearchQueryGreedyConverter(String argName, String description, String defaultString) {
        super(argName, description, defaultString, String.class);
        isGreedy = true;
    }

    @Override
    public Converter<String> convert(String toConvert, Context ctx) throws IllegalArgumentException {

        if (toConvert.startsWith("http://") || toConvert.startsWith("https://"))
        {
            set(toConvert);
        }
        else
        {
            toConvert = "ytsearch:" + toConvert;
            set(toConvert);
        }
        return this;
    }
}
