package com.progmise.utils.enums

interface EnumCompanion<T : Enum<T>>

interface EnumUtil {
    val type: String
    val name: String
}

inline fun <reified T> EnumCompanion<T>.fromValue(value: String): T? where T : Enum<T>, T : EnumUtil =
    enumValues<T>().firstOrNull { it.name == value.uppercase() }

inline fun <reified T> EnumCompanion<T>.fromCode(value: String): T? where T : Enum<T>, T : EnumUtil =
    enumValues<T>().firstOrNull { it.type == value.uppercase() }

inline fun <reified T> EnumCompanion<T>.types(): ArrayList<String> where T : Enum<T>, T : EnumUtil =
    ArrayList(enumValues<T>().map { it.type })
