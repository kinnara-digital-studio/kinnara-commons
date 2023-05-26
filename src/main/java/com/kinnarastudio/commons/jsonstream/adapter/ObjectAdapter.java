package com.kinnarastudio.commons.jsonstream.adapter;

import java.util.Iterator;

/**
 * Adapter for Json Object
 *
 * @param <J>    Json Object class
 */
public interface ObjectAdapter<J, V> {
    J initialize();

    /**
     * Get key iterator for jsonObject object
     *
     * @param jsonObject
     * @return
     */
    Iterator<String> getKeyIterator(J jsonObject);

    boolean objectHasKey(J json, String key);
    /**
     * Get value of particular key
     *
     * @param json
     * @param key
     * @return
     */
    V getValue(J json, String key);

    void putValue(J json, String key, V value);
}
