package io.github.progmise.utils.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private JsonMapper() {
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize value to JSON", e);
        }
    }

    public static <T> T toObject(String json, Class<T> responseClass) {
        try {
            return OBJECT_MAPPER.readValue(json, responseClass);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize JSON", e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> responseClass) {
        try {
            return OBJECT_MAPPER.readValue(json, responseClass);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize JSON", e);
        }
    }
}
