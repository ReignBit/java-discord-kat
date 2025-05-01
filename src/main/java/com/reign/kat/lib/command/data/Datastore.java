package com.reign.kat.lib.command.data;

import com.reign.api.kat.models.ApiGuild;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Datastore {
    private final Map<String, DatastoreField<?>> fields = new HashMap<>();

    public <T> Datastore addField(String fieldName, DatastoreField<T> type) {
        fields.put(fieldName, type);
        return this;
    }

    public Iterator<Map.Entry<String, DatastoreField<?>>> getFields() {
        return fields.entrySet().iterator();
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(String fieldName, ApiGuild guild) {
        return (T) guild.commandData.getOrDefault(fieldName, null);
    }

    public <T> void updateField(String fieldName, T value, ApiGuild guild) {
        guild.commandData.put(fieldName, value);
        guild.save();
    }
}
