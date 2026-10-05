package io.github.progmise.commons.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;

public class NoOpCache implements Cache {

    @Override
    public <T> T get(String key, Class<T> responseClass) {
        return null;
    }

    @Override
    public <T> T getObject(String key, TypeReference<T> responseClass) {
        return null;
    }

    @Override
    public <T> void put(String key, T value) {
    }

    @Override
    public <T> void fastPut(String key, T value) {
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public void fastRemove(String key) {
    }

    @Override
    public void evictData() {
    }
}
