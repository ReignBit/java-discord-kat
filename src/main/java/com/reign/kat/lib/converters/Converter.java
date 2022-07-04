package com.reign.kat.lib.converters;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings({"unchecked"})
public abstract class Converter<T> {
    private static final Logger log = LoggerFactory.getLogger(Converter.class);

    private final Class<?> type;
    private T item;
    public T defaultObject;
    public String argName;
    public String description;
    public boolean optional;

    public Converter(String argName, String description, T defaultObject , Class<?> type) {
        this.argName = argName;
        this.description = description;
        // If converter is supplied a default value then it is optional.
        // otherwise we need to get angry if convert() sets item == null.
        this.optional = (defaultObject != null);
        this.defaultObject = defaultObject;

        this.type = type;
    }

    public abstract Converter<T> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException;
    public String getType()
    {
        return type.getSimpleName();
    }

    public <T> T get() { return (T) item; }
    public void set(T obj) { item = obj; }

    public void setDefault() {
        set(defaultObject);
    }

    public void setDefault(T item) {
        set(item);
    }
}
