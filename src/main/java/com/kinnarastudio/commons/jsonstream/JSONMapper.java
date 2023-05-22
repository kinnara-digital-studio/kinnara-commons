package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.stream.Stream;

/**
 * @author aristo
 *
 * Json Mapper
 */
public final class JSONMapper {
    /**
     * Concatenate {@link JSONArray}s
     *
     * @param array1
     * @param array2
     * @return
     */
    public static JSONArray concat(JSONArray array1, JSONArray array2) {
        return Stream
                .concat(JSONStream.of(array1, Try.onBiFunction(JSONArray::get)), JSONStream.of(array2, Try.onBiFunction(JSONArray::get)))
                .collect(JSONCollectors.toJSONArray());
    }

    /**
     * Combine {@link JSONObject}s
     *
     * @param left
     * @param right
     * @return
     */
    public static JSONObject combine(JSONObject left, JSONObject right) {
        return Stream.concat(JSONStream.of(left, Try.onBiFunction(JSONObject::get)), JSONStream.of(right, Try.onBiFunction(JSONObject::get)))
                .collect(JSONCollectors.toJSONObject(JSONObjectEntry::getKey, JSONObjectEntry::getValue));
    }
}
