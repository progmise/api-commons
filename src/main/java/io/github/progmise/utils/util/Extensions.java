package io.github.progmise.utils.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public final class Extensions {

    private Extensions() {
    }

    public static boolean isNotNullAndBlank(String value) {
        return value != null && !value.isBlank();
    }

    public static <R> R ifNotNullAndBlank(String value, Function<String, R> callback) {
        return isNotNullAndBlank(value) ? callback.apply(value) : null;
    }

    public static <T> T alsoIfNull(T value, Runnable callback) {
        if (value == null) {
            callback.run();
        }
        return value;
    }

    public static LocalDate toLocalDate(String value, String pattern) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> TypeReference<T> typeRef() {
        return new TypeReference<>() {
        };
    }

    public static <T extends Enum<T>> T[] getEnumValues(Class<T> enumType) {
        return enumType.getEnumConstants();
    }
}
