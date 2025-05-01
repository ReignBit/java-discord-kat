package com.reign.kat.lib.command.data;

public class DatastoreField<T> {
    T defaultValue;
    T item;
    Class<T> type;

    @SuppressWarnings("unchecked")
    public DatastoreField(T defaultValue)
    {
        this.defaultValue = defaultValue;
        this.type = (Class<T>) defaultValue.getClass();
    }

    public String typeString() {
        return type.toString();
    }
}
