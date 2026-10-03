package com.progmise.utils.util

import com.fasterxml.jackson.core.type.TypeReference
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

inline fun <T : Any> T.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(javaClass) }

inline fun <T, R> T?.ifNotNullAndBlank(callback: (T) -> R): R? where T : String? = if (isNotNullAndBlank()) this?.let(callback) else null

fun String?.isNotNullAndBlank(): Boolean = this.isNullOrBlank().not()

inline fun <T : Any> T?.alsoIfNull(callback: () -> Unit): T? {
    this ?: callback()
    return this
}

inline fun <reified T> typeRef(): TypeReference<T> = object : TypeReference<T>() {}

fun String?.toLocalDate(pattern: String): LocalDate? =
    try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        null
    }

fun <T : Enum<T>> getEnumValues(enumType: Class<T>): Array<T> = enumType.enumConstants
