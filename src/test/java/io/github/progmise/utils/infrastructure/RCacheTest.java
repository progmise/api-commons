package io.github.progmise.utils.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class RCacheTest {

    private final RedissonClient redissonClient = mock(RedissonClient.class);
    private final RMapCache<String, String> mapCache = mock(RMapCache.class);

    private RCache cache(boolean enabled) {
        return new RCache(redissonClient, "test-collection", 60, TimeUnit.SECONDS, () -> enabled);
    }

    @Test
    void putSerializesValueIntoTheCollectionMap() {
        doReturn(mapCache).when(redissonClient).getMapCache("test-collection");

        cache(true).put("k1", Map.of("id", 7));

        verify(mapCache).put("k1", "{\"id\":7}", 60L, TimeUnit.SECONDS);
    }

    @Test
    void getDeserializesStoredJson() {
        doReturn(mapCache).when(redissonClient).getMapCache("test-collection");
        when(mapCache.get("k1")).thenReturn("{\"id\":7}");

        Map<String, Integer> value = cache(true).getObject("k1", new TypeReference<>() {
        });

        assertEquals(Map.of("id", 7), value);
    }

    @Test
    void getReturnsNullOnCacheMiss() {
        doReturn(mapCache).when(redissonClient).getMapCache("test-collection");

        assertNull(cache(true).getObject("missing", new TypeReference<String>() {
        }));
    }

    @Test
    void doesNotTouchRedisWhenDisabled() {
        RCache disabled = cache(false);

        disabled.put("k1", "v");
        String result = disabled.getObject("k1", new TypeReference<>() {
        });

        assertNull(result);
        verify(redissonClient, never()).getMapCache(anyString());
    }

    @Test
    void returnsNullWhenRedisThrows() {
        doThrow(new RuntimeException("connection refused")).when(redissonClient).getMapCache("test-collection");

        assertNull(cache(true).getObject("k1", new TypeReference<String>() {
        }));
    }
}
