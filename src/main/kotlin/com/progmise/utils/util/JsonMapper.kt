package com.progmise.utils.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonMapper {
    @PublishedApi
    internal val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    fun Any.toJson(): String = objectMapper.writeValueAsString(this)

    inline fun <reified T> String.toObject(): T = objectMapper.readValue(this, T::class.java)
}
