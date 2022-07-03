package com.reign.kat.lib.converters;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@SuppressWarnings({"unchecked"})
public abstract class Converter<T> {
    private T item = null;
    public String argName;
    public String description;
    public boolean optional;

    public Converter(String argName, String description, boolean optional) {
        this.argName = argName;
        this.description = description;
        this.optional = optional;
    }

    public abstract Converter<T> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException;

    public <T> T get() { return (T) item; }
    public void set(T obj) { item = obj; }

}