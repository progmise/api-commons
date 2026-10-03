package io.github.progmise.utils.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;

public interface Cache {

    <T> T get(String key, Class<T> responseClass);

    <T> T getObject(String key, TypeReference<T> responseClass);

    <T> void put(String key, T value);

    <T> void fastPut(String key, T value);

    void remove(String key);

    void fastRemove(String key);

    void evictData();
}
