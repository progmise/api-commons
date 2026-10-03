package com.progmise.utils.infrastructure

import com.fasterxml.jackson.core.type.TypeReference

class NoOpCache : Cache {
    override fun <T> get(
        key: String,
        responseClass: Class<T>,
    ): T? = null

    override fun <T> getObject(
        key: String,
        responseClass: TypeReference<T>,
    ): T? = null

    override fun <T> put(
        key: String,
        value: T,
    ) = Unit

    override fun <T> fastPut(
        key: String,
        value: T,
    ) = Unit

    override fun remove(key: String) = Unit

    override fun fastRemove(key: String) = Unit

    override fun evictData() = Unit
}
