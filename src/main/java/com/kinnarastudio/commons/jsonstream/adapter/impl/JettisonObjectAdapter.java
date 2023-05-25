package com.kinnarastudio.commons.jsonstream.adapter.impl;

import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;

public class JettisonObjectAdapter implements ObjectAdapter<JSONObject> {
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
    public <V> V getValue(JSONObject json, String key) {
        try {
            return (V) json.get(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> void putValue(JSONObject json, String key, V value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
