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
    public static <J, V> J concat(ArrayAdapter<J, V> adapter, J array1, J array2) {
        return Stream
                .concat(JSONStream.of(adapter, array1), JSONStream.of(adapter, array2))
                .collect(JSONCollectors.toJson(adapter));
    }

    public static <T> JSONArray concat(JSONArray array1, JSONArray array2) {
        final JSONArrayAdapter<T> adapter = new JSONArrayAdapter<>();
        return concat(adapter, array1, array2);
    }

    /**
     * Compine json objects
     *
     * @param left
     * @param right
     * @return
     */
    public static <J, T> J combine(ObjectAdapter<J> adapter, J left, J right) {
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
    public static <T> JSONObject combine(JSONObject left, JSONObject right) {
        final JSONObjectAdapter adapter = new JSONObjectAdapter();
        return combine(adapter, left, right);
    }
}
