package com.kinnarastudio.commons.jsonstream.adapter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Adapter for json array
 *
 * @param <JSON>    Json Array class
 * @param <VALUE>   Value type to be extracted
 */
public interface JsonArrayStreamerAdapter<JSON, VALUE> {
    /**
     * Get elements length of json array
     *
     * @param json
     * @return
     */
    int getLength(JSON json);

    /**
     * Get jsonArray array value at specific index
     *
     * @param jsonArray
     * @param index
     * @return
     */
    VALUE getValue(JSON jsonArray, int index);
}
