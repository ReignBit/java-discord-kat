package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.Context;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unchecked"})
public abstract class Converter<T> {
    private static final Logger log = LoggerFactory.getLogger(Converter.class);

    private final Class<?> type;
    private OptionType optionType = OptionType.STRING;
    private T item;
    public T defaultObject;
    public String argName;
    public String description;
    public boolean optional;
    public boolean isGreedy = false;





    public Converter(String argName, String description, T defaultObject , Class<?> type) {
        this.argName = argName;
        this.description = description;
        // If converter is supplied a default value then it is optional.
        // otherwise we need to get angry if convert() sets item == null.
        this.optional = (defaultObject != null);
        this.defaultObject = defaultObject;

        this.type = type;
    }

    public Converter(String argName, String description, T defaultObject, Class<?> type, OptionType optionType)
    {
        this.argName = argName;
        this.description = description;
        // If converter is supplied a default value then it is optional.
        // otherwise we need to get angry if convert() sets item == null.
        this.optional = (defaultObject != null);
        this.defaultObject = defaultObject;

        this.optionType = optionType;
        this.type = type;
    }

    public abstract Converter<T> convert(String toConvert, Context event) throws IllegalArgumentException;

    public OptionData getSlashOptionData()
    {
        return new OptionData(optionType, argName, description)
                .setRequired(!optional);

    }

    public String getType()
    {
        return type.getSimpleName();
    }

    public <U> U get() { return (U)item; }
    public void set(T obj) { item = obj; }

    public void setDefault() {
        set(defaultObject);
    }

    public void setDefault(T item) {
        set(item);
    }

    public void setOptional(boolean isOptional)
    {
        this.optional = isOptional;
    }

    public void setGreedy(boolean greedy) {
        isGreedy = greedy;
    }
}
