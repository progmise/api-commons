package com.progmise.utils.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.progmise.utils.util.typeRef
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.redisson.api.RMapCache
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RCacheTest {
    private val redissonClient: RedissonClient = mock()
    private val mapCache: RMapCache<String, String> = mock()

    private fun cache(enabled: Boolean = true) =
        RCache(
            redissonClient = redissonClient,
            redisCollection = "test-collection",
            timeToLive = 60,
            unit = TimeUnit.SECONDS,
            isRedisEnabled = { enabled },
        )

    @Test
    fun `put serializes value into the collection map`() {
        whenever(redissonClient.getMapCache<String, String>("test-collection")).thenReturn(mapCache)

        cache().put("k1", mapOf("id" to 7))

        verify(mapCache).put(eq("k1"), eq("""{"id":7}"""), eq(60L), eq(TimeUnit.SECONDS))
    }

    @Test
    fun `get deserializes stored json`() {
        whenever(redissonClient.getMapCache<String, String>("test-collection")).thenReturn(mapCache)
        whenever(mapCache["k1"]).thenReturn("""{"id":7}""")

        val value = cache().getObject("k1", typeRef<Map<String, Int>>())

        assertEquals(mapOf("id" to 7), value)
    }

    @Test
    fun `get returns null on cache miss`() {
        whenever(redissonClient.getMapCache<String, String>("test-collection")).thenReturn(mapCache)
        whenever(mapCache["missing"]).thenReturn(null)

        assertNull(cache().getObject("missing", object : TypeReference<String>() {}))
    }

    @Test
    fun `does not touch redis when disabled`() {
        val disabled = cache(enabled = false)

        disabled.put("k1", "v")
        val result = disabled.getObject("k1", typeRef<String>())

        assertNull(result)
        verify(redissonClient, never()).getMapCache<String, String>(any<String>())
    }

    @Test
    fun `returns null when redis throws`() {
        whenever(redissonClient.getMapCache<String, String>("test-collection")).thenThrow(RuntimeException("connection refused"))

        assertNull(cache().getObject("k1", typeRef<String>()))
    }
}
