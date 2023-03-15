package com.reign.api.lib.providers;

import com.reign.api.kat.models.ApiModel;
import com.reign.api.kat.responses.ApiResponse;



import java.util.HashMap;

/*
    TODO:
    For this to work we need to remove the Http api from our model classes
    for example, we have properties like `data,msg,error,status`. Instead these
    should be the data types for the model:
        guildID
        memberCount
        etc.

    Gonna leave this as a stub for now and SHOULD NOT BE USED.

 */
public class ApiLocalProvider implements IApiProvider
{

    public static class Pair<T,Y>
    {
        public T key;
        public Y value;

        public Pair(T key, Y value)
        {
            this.key = key;
            this.value = value;
        }

        public T getKey() { return key; }
        public Y getValue() { return value; }
    }

    private final HashMap<String, Pair<String, Class<?>>> mem = new HashMap<>();

    @Override
    public <T extends ApiModel, Y extends ApiResponse<?>> boolean commit(String endpoint, T apiModel, Class<Y> responseClass)
    {
        //mem.put(endpoint, new Pair<>(apiModel.toString(), responseClass));
        return false;
    }

    @Override
    public <Y extends ApiResponse<?>> Y fetch(String endpoint, Class<Y> responseClass)
    {
        //Pair<String, Class<Y>> result = mem.get(endpoint);
        return null;
    }
}
