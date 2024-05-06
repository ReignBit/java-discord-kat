package com.reign.api.kat;


import com.reign.api.lib.Pair;
import com.reign.kat.lib.utils.stats.BotStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;

// TODO: Add deferred saving with optional max defer time.
public class ApiCache<T>
{
    private static final Logger log = LoggerFactory.getLogger(ApiCache.class);
    private int hitCount = 0;
    private int missCount = 0;


    public Duration lifetime;
    final Class<T> type;
    final HashMap<String, Pair<T, Long>> cache = new HashMap<>();

    public float getHitRate() { return (float)hitCount / ((float)hitCount + (float)missCount); }

    public ApiCache(Class<T> cls, Duration lifetime)
    {
        type = cls;
        this.lifetime = lifetime;
        log.info("Created ApiCache for type {} with lifetime {} seconds", type, lifetime.toSeconds());
        BotStats.addToReport(cls.getSimpleName() + " Cache Hit Rate (%)", this::getHitRate);
    }

    public ApiCache(Class<T> cls)
    {
        type = cls;
        lifetime = Duration.ofMinutes(5);
        log.info("Created ApiCache for type {} with lifetime {} seconds", type, lifetime.toSeconds());
        BotStats.addToReport(cls.getSimpleName() + " Cache Hit Rate (%)", this::getHitRate);
    }

    public T upsert(String key, T value)
    {
        log.debug("Inserted Key '{}'", key);
        cache.put(key, new Pair<>(value, getCurrentTime()));
        return value;
    }

    /**
     * Get a value from the cache.
     * @param key String key of value.
     * @return Stored value or null if TTL expired.
     */
    public T get(String key)
    {
        if (cache.containsKey(key))
        {
            Pair<T, Long> entry = cache.get(key);
            if (!isDirty(key))
            {
                log.info("Cache hit! Key '{}' valid for another {} seconds", key, getTimeLeftUntilDirty(key));
                hitCount++;
                return entry.getKey();
            }
            else
            {
                log.warn("Dirty key in cache Key '{}' {} >= {}", key, entry.getValue(), lifetime.toMillis());
            }
        }
        log.warn("Cache miss for key '{}'", key);
        missCount++;
        return null;
    }

    public void remove(String key)
    {
        cache.remove(key);
        log.info("Removed Key '{}'", key);
    }

    public void clear()
    {
        int size = cache.size();
        cache.clear();

        log.warn("Cleared cache of {} items", size);
    }

    private long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

    private long getTimeLeftUntilDirty(String key)
    {
        Pair<T, Long> entry = cache.getOrDefault(key, null);
        return (lifetime.toMillis() - (getCurrentTime() - entry.getValue())) / 1000;
    }

    private boolean isDirty(String key)
    {
        Pair<T, Long> entry = cache.getOrDefault(key, null);
        if (entry == null) { return false; }

        return (getCurrentTime() - entry.getValue() >= lifetime.toMillis());
    }
}
