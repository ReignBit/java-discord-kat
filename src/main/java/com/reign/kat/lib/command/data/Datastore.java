package com.reign.kat.lib.command.data;

import com.reign.api.kat.models.ApiGuild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Datastore {
    private final Map<String, DatastoreField<?>> fields = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(Datastore.class);

    public <T> Datastore addField(String fieldName, DatastoreField<T> type) {
        fields.put(fieldName, type);
        return this;
    }

    public Iterator<Map.Entry<String, DatastoreField<?>>> getFields() {
        return fields.entrySet().iterator();
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(String fieldName, ApiGuild guild) {
        return (T) guild.commandData.getOrDefault(fieldName, fields.get(fieldName).defaultValue);
    }

    public <T> void updateField(String fieldName, T value, ApiGuild guild) {
        log.debug("Datastore {}: {} = {}", fieldName, value, guild.snowflake);
        guild.commandData.put(fieldName, value);
        guild.save();
    }
}
