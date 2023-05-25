package com.kinnarastudio.commons.jsonstream.adapter.impl;

import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONObjectAdapter implements ObjectAdapter<JSONObject> {
    @Override
    public JSONObject initialize() {
        return new JSONObject();
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
    public <T> T getValue(JSONObject jsonObject, String key) {
        return (T) jsonObject.get(key);
    }

    @Override
    public <T> void putValue(JSONObject json, String key, T value) {
        json.put(key, value);
    }
}
