package com.progmise.utils.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.progmise.utils.util.alsoIfNull
import com.progmise.utils.util.logger
import org.redisson.api.RMapCache
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

open class RCache(
    private val redissonClient: RedissonClient,
    private val redisCollection: String,
    private val timeToLive: Long,
    private val unit: TimeUnit = TimeUnit.SECONDS,
    private val isRedisEnabled: () -> Boolean = { true },
) : Cache {
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    private val log by logger()

    override fun <T> get(
        key: String,
        responseClass: Class<T>,
    ): T? {
        val startTime = System.currentTimeMillis()
        return getFromCache(key, startTime) {
            try {
                objectMapper.readValue(it, responseClass)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun <T> getObject(
        key: String,
        responseClass: TypeReference<T>,
    ): T? {
        val startTime = System.currentTimeMillis()
        return getFromCache(key, startTime) {
            try {
                objectMapper.readValue(it, responseClass)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun <T> getFromCache(
        key: String,
        startTime: Long,
        deserializer: (String) -> T?,
    ): T? {
        if (!checkRedisEnabled(startTime, key)) return null
        val value =
            try {
                mapCache()[key]
            } catch (e: Exception) {
                log.warn("[CACHE] Error reading key {}: {}", key, e.message)
                null
            }?.let { deserializer(it) }
        logResponseTime(startTime, key)
        return value.alsoIfNull { log.debug("[CACHE] Cache miss for key {}", key) }
    }

    override fun <T> put(
        key: String,
        value: T,
    ) {
        if (!isEnabled()) return
        mapCache().put(key, serialize(value), timeToLive, unit)
    }

    override fun <T> fastPut(
        key: String,
        value: T,
    ) {
        if (!isEnabled()) return
        mapCache().fastPut(key, serialize(value), timeToLive, unit)
    }

    override fun remove(key: String) = fastRemove(key)

    override fun fastRemove(key: String) {
        if (!isEnabled()) return
        mapCache().fastRemove(key)
    }

    override fun evictData() {
        if (!isEnabled()) return
        mapCache().clear()
    }

    open fun <T> serialize(value: T): String? =
        try {
            objectMapper.writeValueAsString(value)
        } catch (e: Exception) {
            log.warn("[CACHE] Warning: value will not be stored for collection {}: {}", redisCollection, e.message)
            null
        }

    private fun mapCache(): RMapCache<String, String> = redissonClient.getMapCache(redisCollection)

    private fun isEnabled(): Boolean =
        try {
            isRedisEnabled()
        } catch (e: Exception) {
            false
        }

    private fun checkRedisEnabled(
        startTime: Long,
        key: String,
    ): Boolean {
        val enabled = isEnabled()
        if (!enabled) logResponseTime(startTime, key)
        return enabled
    }

    private fun logResponseTime(
        startTime: Long,
        key: String,
    ) {
        val responseTime = System.currentTimeMillis() - startTime
        if (responseTime > MAXIMUM_ACCEPTABLE_RESPONSE_TIME) {
            log.warn("Cache response time for key {}: {} ms", key, responseTime)
        }
    }

    companion object {
        private const val MAXIMUM_ACCEPTABLE_RESPONSE_TIME = 200L
    }
}
