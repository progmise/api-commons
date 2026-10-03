package io.github.progmise.utils.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class RCache implements Cache {

    private static final long MAXIMUM_ACCEPTABLE_RESPONSE_TIME = 200L;

    private static final Logger log = LoggerFactory.getLogger(RCache.class);

    private final RedissonClient redissonClient;
    private final String redisCollection;
    private final long timeToLive;
    private final TimeUnit unit;
    private final Supplier<Boolean> isRedisEnabled;
    private final ObjectMapper objectMapper;

    public RCache(
        RedissonClient redissonClient,
        String redisCollection,
        long timeToLive,
        TimeUnit unit,
        Supplier<Boolean> isRedisEnabled
    ) {
        this.redissonClient = redissonClient;
        this.redisCollection = redisCollection;
        this.timeToLive = timeToLive;
        this.unit = unit;
        this.isRedisEnabled = isRedisEnabled;
        this.objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
            .registerModule(new JavaTimeModule());
    }

    public RCache(RedissonClient redissonClient, String redisCollection, long timeToLive) {
        this(redissonClient, redisCollection, timeToLive, TimeUnit.SECONDS, () -> true);
    }

    @Override
    public <T> T get(String key, Class<T> responseClass) {
        long startTime = System.currentTimeMillis();
        return getFromCache(key, startTime, value -> deserialize(value, responseClass));
    }

    @Override
    public <T> T getObject(String key, TypeReference<T> responseClass) {
        long startTime = System.currentTimeMillis();
        return getFromCache(key, startTime, value -> deserialize(value, responseClass));
    }

    private <T> T getFromCache(String key, long startTime, Function<String, T> deserializer) {
        if (!checkRedisEnabled(startTime, key)) {
            return null;
        }

        String cacheValue;
        try {
            cacheValue = mapCache().get(key);
        } catch (Exception e) {
            log.warn("[CACHE] Error reading key {}: {}", key, e.getMessage());
            return null;
        }

        T value = cacheValue == null ? null : deserializer.apply(cacheValue);
        logResponseTime(startTime, key);

        if (value == null) {
            log.debug("[CACHE] Cache miss for key {}", key);
        }

        return value;
    }

    @Override
    public <T> void put(String key, T value) {
        if (!isEnabled()) {
            return;
        }
        String serialized = serialize(value);
        if (serialized != null) {
            mapCache().put(key, serialized, timeToLive, unit);
        }
    }

    @Override
    public <T> void fastPut(String key, T value) {
        if (!isEnabled()) {
            return;
        }
        String serialized = serialize(value);
        if (serialized != null) {
            mapCache().fastPut(key, serialized, timeToLive, unit);
        }
    }

    @Override
    public void remove(String key) {
        fastRemove(key);
    }

    @Override
    public void fastRemove(String key) {
        if (!isEnabled()) {
            return;
        }
        mapCache().fastRemove(key);
    }

    @Override
    public void evictData() {
        if (!isEnabled()) {
            return;
        }
        mapCache().clear();
    }

    public <T> String serialize(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.warn(
                "[CACHE] Warning: value will not be stored for collection {}: {}",
                redisCollection,
                e.getMessage()
            );
            return null;
        }
    }

    private <T> T deserialize(String value, Class<T> responseClass) {
        try {
            return objectMapper.readValue(value, responseClass);
        } catch (Exception e) {
            return null;
        }
    }

    private <T> T deserialize(String value, TypeReference<T> responseClass) {
        try {
            return objectMapper.readValue(value, responseClass);
        } catch (Exception e) {
            return null;
        }
    }

    private RMapCache<String, String> mapCache() {
        return redissonClient.getMapCache(redisCollection);
    }

    private boolean isEnabled() {
        try {
            return Boolean.TRUE.equals(isRedisEnabled.get());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkRedisEnabled(long startTime, String key) {
        boolean enabled = isEnabled();
        if (!enabled) {
            logResponseTime(startTime, key);
        }
        return enabled;
    }

    private void logResponseTime(long startTime, String key) {
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > MAXIMUM_ACCEPTABLE_RESPONSE_TIME) {
            log.warn("Cache response time for key {}: {} ms", key, responseTime);
        }
    }
}
