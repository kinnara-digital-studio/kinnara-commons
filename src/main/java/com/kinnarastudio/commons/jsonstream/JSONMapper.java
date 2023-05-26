package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.jsonstream.adapter.ArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JSONArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JSONObjectAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.stream.Stream;

/**
 * @author aristo
 *
 * Json Mapper
 */
public final class JSONMapper {
    public static <J, V> J concat(ArrayAdapter<J, V> adapter, J left, J right) {
        return Stream
                .concat(JSONStream.of(adapter, left), JSONStream.of(adapter, right))
                .collect(JSONCollectors.toJson(adapter));
    }

    public static <T> JSONArray concat(JSONArray left, JSONArray right) {
        final JSONArrayAdapter<T> adapter = new JSONArrayAdapter<>();
        return concat(adapter, left, right);
    }

    /**
     * Compine json objects
     *
     * @param left
     * @param right
     * @return
     */
    public static <J, V> J combine(ObjectAdapter<J, V> adapter, J left, J right) {
        return Stream.concat(JSONStream.of(adapter, left), JSONStream.of(adapter, right))
                .collect(JSONCollectors.toJson(adapter, JSONObjectEntry::getKey, JSONObjectEntry::getValue));
    }

    /**
     * Combine {@link JSONObject}s
     *
     * @param left
     * @param right
     * @return
     */
    public static <V> JSONObject combine(JSONObject left, JSONObject right) {
        final JSONObjectAdapter<V> adapter = new JSONObjectAdapter<>();
        return combine(adapter, left, right);
    }
}
