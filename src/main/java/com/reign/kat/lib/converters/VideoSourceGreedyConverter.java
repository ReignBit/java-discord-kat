package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoSourceGreedyConverter extends Converter<String> {
    public static final Logger log = LoggerFactory.getLogger(StringConverter.class);
    private static final String SEARCH_OVERRIDE_STR = "!src";

    public VideoSourceGreedyConverter(String argName, String description, String defaultString) {
        super(argName, description, defaultString, String.class);
        isGreedy = true;
    }

    /**
     * Greedily converts into a Lavaplayer searchable string,
     * defaults to ytsearch if not overriden. Links do not require a search provider.
     * Will be overriden if `toConvert` starts with `!src`.
     * @param toConvert input search string
     * @param ctx   message context
     * @return  Lavaplayer search formatted string
     */
    @Override
    public Converter<String> convert(String toConvert, Context ctx) throws IllegalArgumentException {

        // We should default to searching via youtube (ytsearch:) unless the arg is a link, or user wants
        // to override it with !src, example of provider: !srcscsearch - soundcloud search
        if (toConvert == null) {
            setDefault();
            return this;
        }
        if (toConvert.startsWith(SEARCH_OVERRIDE_STR))
        {
            log.debug("other !src");
            toConvert = toConvert.replace(SEARCH_OVERRIDE_STR, "");
        }
        else if (!toConvert.startsWith("http://") && !toConvert.startsWith("https://"))
        {
            log.debug("ytsearch");
            toConvert = "ytsearch:" + toConvert;
        }

        set(toConvert);
        return this;
    }
}
