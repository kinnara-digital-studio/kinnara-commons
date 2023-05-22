package com.kinnarastudio.commons.adapter;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.function.BiFunction;

public class JSONObjectStreamerAdapter<V> implements StreamerAdapter<JSONObject, String> {
    private final BiFunction<JSONObject, String, V> valueExtractor;

    public JSONObjectStreamerAdapter(BiFunction<JSONObject, String, V> valueExtractor) {
        this.valueExtractor = valueExtractor;
    }

    @Override
    public Iterator<String> getKeyIterator(JSONObject jsonObject) {
        return jsonObject.keys();
    }
}
