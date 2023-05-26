package com.kinnarastudio.commons.jsonstream.adapter.impl;

import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.adapter.ArrayAdapter;
import org.codehaus.jettison.json.JSONArray;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class JettisonArrayAdapter<V> implements ArrayAdapter<JSONArray, V> {
    final Supplier<JSONArray> initializer;
    final BiFunction<JSONArray, Integer, V> valueExtractor;

    public JettisonArrayAdapter() {
        this(() -> new JSONArray());
    }

    public JettisonArrayAdapter(Supplier<JSONArray> initializer) {
        this(initializer, Try.onBiFunction((jsonArray, index) -> (V) jsonArray.get(index)));
    }

    public JettisonArrayAdapter(BiFunction<JSONArray, Integer, V> valueExtractor) {
        this(JSONArray::new, valueExtractor);
    }

    public JettisonArrayAdapter(Supplier<JSONArray> initializer, BiFunction<JSONArray, Integer, V> valueExtractor) {
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
    public int getLength(JSONArray json) {
        return json.length();
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
