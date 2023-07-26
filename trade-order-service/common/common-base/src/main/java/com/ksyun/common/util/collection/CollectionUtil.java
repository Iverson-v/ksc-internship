package com.ksyun.common.util.collection;

import org.apache.commons.collections4.map.Flat3Map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public final class CollectionUtil {

    public static <K, V> Flat3Map<K, V> flat3Map(K key1, V value1, K key2, V value2) {
        Flat3Map<K, V> map = new Flat3Map<K, V>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static <K, V> Flat3Map<K, V> flat3Map(K key1, V value1, K key2, V value2, K key3, V value3) {
        Flat3Map<K, V> map = new Flat3Map<K, V>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    /**
     * 判断是否为空.
     */
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return (map != null) && !map.isEmpty();
    }

    public static <K, V> HashMap<K, V> newHashMapWithCapacity(int expectedSize, float loadFactor) {
        int finalSize = (int) ((double) expectedSize / loadFactor + 1.0F);
        return new HashMap<K, V>(finalSize, loadFactor);
    }

    private CollectionUtil() {
    }
}
