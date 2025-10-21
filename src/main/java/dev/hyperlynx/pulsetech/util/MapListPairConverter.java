package dev.hyperlynx.pulsetech.util;

import com.mojang.datafixers.util.Pair;

import java.util.*;

public class MapListPairConverter<K, V> {
    public List<Pair<K, V>> fromMap(Map<K, V> map) {
        List<Pair<K, V>> entries = new ArrayList<>();
        for(K key : map.keySet()) {
            entries.add(Pair.of(key, map.get(key)));
        }
        return entries;
    }

    public Map<K, V> toMap(List<Pair<K, V>> pairs) {
        Map<K, V> map = new HashMap<>();
        for(Pair<K, V> pair : pairs) {
            map.put(pair.getFirst(), pair.getSecond());
        }
        return map;
    }
}
