package com.kinnarastudio.commons.jsonstream.adapter.impl;

import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class JettisonObjectAdapter<V> implements ObjectAdapter<JSONObject, V> {
    final Supplier<JSONObject> initializer;

    final BiFunction<JSONObject, String, V> valueExtractor;
    public JettisonObjectAdapter() {
        this(() -> new JSONObject());
    }

    public JettisonObjectAdapter(Supplier<JSONObject> initializer) {
        this(initializer, Try.onBiFunction((jsonObject, key) -> (V) jsonObject.get(key)));
    }

    public JettisonObjectAdapter(BiFunction<JSONObject, String, V> valueExtractor) {
        this(JSONObject::new, valueExtractor);
    }

    public JettisonObjectAdapter(Supplier<JSONObject> initializer, BiFunction<JSONObject, String, V> valueExtractor) {
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
    public V getValue(JSONObject json, String key) {
        return valueExtractor.apply(json, key);
    }

    @Override
    public void putValue(JSONObject json, String key, V value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
