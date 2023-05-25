package com.kinnarastudio.commons.jsonstream.adapter;

/**
 *
 * @param <J> Json array class
 */
public interface ArrayAdapter<J, V> {
    J instantiate();

    /**
     * Get elements length of json array
     *
     * @param json
     * @return
     */
    int getLength(J json);

    /**
     * Get jsonArray array value at specific index
     *
     * @param jsonArray
     * @param index
     * @return
     */
    V getValue(J jsonArray, int index);

    void putValue(J jsonArray, V value);
}
