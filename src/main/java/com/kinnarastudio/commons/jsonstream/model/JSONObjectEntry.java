package com.kinnarastudio.commons.jsonstream.model;

import java.util.AbstractMap;
import java.util.Map;

/**
 * @author aristo
 *
 * Entry for JSONObject
 *
 * @param <V> json value type
 */
public class JSONObjectEntry<V> extends AbstractMap.SimpleImmutableEntry<String, V> {
    public JSONObjectEntry(String key, V value) {
        super(key, value);
    }

    public JSONObjectEntry(Map.Entry<? extends String, ? extends V> entry) {
        super(entry);
    }

    @Override
    public final String getKey() {
        return super.getKey();
    }

    @Override
    public final V getValue() {
        return super.getValue();
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }
}
