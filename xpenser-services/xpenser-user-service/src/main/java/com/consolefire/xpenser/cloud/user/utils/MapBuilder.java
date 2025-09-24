package com.consolefire.xpenser.cloud.user.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapBuilder<K, V> {

    private final Map<K, V> targetMap = new ConcurrentHashMap<>();

    public static <P, Q> MapBuilder<P, Q> builder() {
        return new MapBuilder<>();
    }

    public MapBuilder<K, V> with(K key, V value) {
        targetMap.put(key, value);
        return this;
    }

    public KeyValueSetter<K, V> forKey(K key) {
        return new KeyValueSetter<>(this, key);
    }

    public Map<K, V> build() {
        return targetMap;
    }

    public class KeyValueSetter<P, Q> {

        private final MapBuilder<P, Q> kvMapBuilder;
        private final P key;

        public KeyValueSetter(MapBuilder<P, Q> kvMapBuilder, P key) {
            this.kvMapBuilder = kvMapBuilder;
            this.key = key;
        }

        public MapBuilder<P, Q> withValue(Q value) {
            kvMapBuilder.with(key, value);
            return kvMapBuilder;
        }
    }

}
