package com.progmise.utils.infrastructure

import com.fasterxml.jackson.core.type.TypeReference

interface Cache {
    fun <T> get(
        key: String,
        responseClass: Class<T>,
    ): T?

    fun <T> getObject(
        key: String,
        responseClass: TypeReference<T>,
    ): T?

    fun <T> put(
        key: String,
        value: T,
    )

    fun <T> fastPut(
        key: String,
        value: T,
    )

    fun remove(key: String)

    fun fastRemove(key: String)

    fun evictData()
}
