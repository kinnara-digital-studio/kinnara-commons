package com.kinnarastudio.commons.jsonstream.adapter.impl;

import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class JSONObjectAdapter<V> implements ObjectAdapter<JSONObject, V> {

    final Supplier<JSONObject> initializer;

    final BiFunction<JSONObject, String, V> valueExtractor;

    public JSONObjectAdapter() {
        this(() -> new JSONObject());
    }
    public JSONObjectAdapter(Supplier<JSONObject> initializer) {
        this(initializer, Try.onBiFunction((jsonObject, key) -> (V) jsonObject.get(key)));
    }

    public JSONObjectAdapter(BiFunction<JSONObject, String, V> valueExtractor) {
        this(JSONObject::new, valueExtractor);
    }
    public JSONObjectAdapter(Supplier<JSONObject> initializer, BiFunction<JSONObject, String, V> valueExtractor) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(valueExtractor);

        this.initializer = initializer;
        this.valueExtractor = valueExtractor;
    }
    @Override
    public JSONObject initialize() {
        return initializer.get();
    }

    @Override
    public Iterator<String> getKeyIterator(JSONObject jsonObject) {
        return jsonObject.keys();
    }

    @Override
    public boolean objectHasKey(JSONObject json, String key) {
        return json.has(key);
    }

    @Override
    public V getValue(JSONObject jsonObject, String key) {
        return valueExtractor.apply(jsonObject, key);
    }

    @Override
    public void putValue(JSONObject json, String key, V value) {
        json.put(key, value);
    }
}
