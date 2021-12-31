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
     * @param object1
     * @param object2
     * @return
     */
    public static JSONObject combine(JSONObject object1, JSONObject object2) {
        return Stream.concat(JSONStream.of(object1, Try.onBiFunction(JSONObject::get)), JSONStream.of(object2, Try.onBiFunction(JSONObject::get)))
                .collect(JSONCollectors.toJSONObject(JSONObjectEntry::getKey, JSONObjectEntry::getValue));
    }

    /**
     * Concatenate {@link org.codehaus.jettison.json.JSONArray}s
     *
     * @param array1
     * @param array2
     * @return
     */
    public static org.codehaus.jettison.json.JSONArray concat(org.codehaus.jettison.json.JSONArray array1, org.codehaus.jettison.json.JSONArray array2) {
        return Stream
                .concat(JSONStream.ofAnotherJson(array1, Try.onBiFunction(org.codehaus.jettison.json.JSONArray::get)), JSONStream.ofAnotherJson(array2, Try.onBiFunction(org.codehaus.jettison.json.JSONArray::get)))
                .collect(JSONCollectors.toAnotherJSONArray());
    }

    /**
     * Combine {@link org.codehaus.jettison.json.JSONObject}s
     *
     * @param object1
     * @param object2
     * @return
     */
    public static org.codehaus.jettison.json.JSONObject combine(org.codehaus.jettison.json.JSONObject object1, org.codehaus.jettison.json.JSONObject object2) {
        return Stream.concat(JSONStream.ofAnotherJson(object1, Try.onBiFunction(org.codehaus.jettison.json.JSONObject::get)), JSONStream.ofAnotherJson(object2, Try.onBiFunction(org.codehaus.jettison.json.JSONObject::get)))
                .collect(JSONCollectors.toAnotherJSONObject(JSONObjectEntry::getKey, JSONObjectEntry::getValue));
    }
}
