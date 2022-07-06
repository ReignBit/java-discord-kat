package com.reign.api;

import java.util.HashMap;


public class ApiCache {

    public static int CACHE_LIFETIME_SECONDS = 600;

    static class CacheObject
    {
        private final Object value;
        private final long expireTime;

        CacheObject(Object value, long expireTime)
        {
            this.value = value;
            this.expireTime = expireTime;
        }

        Object getValue() { return value; }

        boolean isExpired()
        {
            return System.currentTimeMillis() > expireTime;
        }
    }

    private final HashMap<String, CacheObject> cacheHashMap = new HashMap<>();

    public ApiCache(int lifetime)
    {
        CACHE_LIFETIME_SECONDS = lifetime > 0 ? lifetime : CACHE_LIFETIME_SECONDS;
    }

    public Object get(String key)
    {
        if (!cacheHashMap.containsKey(key)) { return null; }
            CacheObject item = cacheHashMap.get(key);
            if (item.isExpired())
            {
                cacheHashMap.remove(key);
                return null;
            }
        return item.getValue();
    }

    public void add(String key, Object item)
    {
        cacheHashMap.put(key, new CacheObject(item, CACHE_LIFETIME_SECONDS));
    }

    public void remove(String key)
    {
        if (!cacheHashMap.containsKey(key)) { return; }
        cacheHashMap.remove(key);
    }

    public void clear()
    {
        cacheHashMap.clear();
    }

    public int size()
    {
        return cacheHashMap.size();
    }
}
