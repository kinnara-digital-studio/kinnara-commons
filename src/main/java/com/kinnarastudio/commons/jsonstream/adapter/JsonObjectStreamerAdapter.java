package com.kinnarastudio.commons.jsonstream.adapter;

import java.util.Iterator;

/**
 * Adapter for Json Object
 *
 * @param <JSON>    Json Object class
 * @param <VALUE>   Value type to be extracted
 */
public interface JsonObjectStreamerAdapter<JSON, VALUE> {
    /**
     * Get key iterator for jsonObject object
     *
     * @param jsonObject
     * @return
     */
    Iterator<String> getKeyIterator(JSON jsonObject);

    /**
     * Get value of particular key
     *
     * @param json
     * @param key
     * @return
     */
    VALUE getValue(JSON json, String key);
}
