package com.kinnarastudio.commons.jsonstream.adapter.impl;

import com.kinnarastudio.commons.jsonstream.adapter.ArrayAdapter;
import org.json.JSONArray;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Default adapter implementation using {@link JSONArray}
 *
 * @param <V> Json array content type
 */
public class JSONArrayAdapter<V> implements ArrayAdapter<JSONArray, V> {
    final Supplier<JSONArray> initializer;
    final BiFunction<JSONArray, Integer, V> valueExtractor;

    public JSONArrayAdapter() {
        this(JSONArray::new);
    }

    public JSONArrayAdapter(Supplier<JSONArray> initializer) {
        this(initializer, (jsonArray, index) -> (V) jsonArray.get(index));
    }

    public JSONArrayAdapter(BiFunction<JSONArray, Integer, V> valueExtractor) {
        this(JSONArray::new, valueExtractor);
    }

    public JSONArrayAdapter(Supplier<JSONArray> initializer, BiFunction<JSONArray, Integer, V> valueExtractor) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(valueExtractor);

        this.initializer = initializer;
        this.valueExtractor = valueExtractor;
    }

    @Override
    public JSONArray instantiate() {
        return initializer.get();
    }

    @Override
    public int getLength(JSONArray jsonArray) {
        return jsonArray.length();
    }

    @Override
    public V getValue(JSONArray jsonArray, int index) {
        return valueExtractor.apply(jsonArray, index);
    }

    @Override
    public void putValue(JSONArray jsonArray, V value) {
        jsonArray.put(value);
    }
}
