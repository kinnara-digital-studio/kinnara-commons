package com.kinnarastudio.commons.jsonstream;

import java.util.AbstractMap;
import java.util.Map;

/**
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
}
